package com.tinhnguyenxanh.service;

import com.tinhnguyenxanh.entity.Event;
import com.tinhnguyenxanh.entity.EventRegistration;
import com.tinhnguyenxanh.entity.Volunteer;
import com.tinhnguyenxanh.repository.EventRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizerVolunteerExportService {

    private final EventRegistrationRepository registrationRepo;
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] buildVolunteersExcel(Integer organizationId) throws IOException {
        List<EventRegistration> all = registrationRepo.findAllByOrganizationId(organizationId);
        all.sort(Comparator
                .comparing((EventRegistration r) -> r.getEvent().getStartTime(), Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(r -> r.getRegisteredDate(), Comparator.nullsLast(Comparator.reverseOrder())));

        Map<Integer, List<EventRegistration>> byVol = all.stream()
                .collect(Collectors.groupingBy(r -> r.getVolunteer().getId(), LinkedHashMap::new, Collectors.toList()));

        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle headerStyle = createHeaderStyle(wb);

            // --- Sheet 1: Mỗi dòng = một đăng ký — rõ sự kiện nào ---
            Sheet detail = wb.createSheet("Chi tiết đăng ký");
            String[] detailHeaders = {
                    "STT",
                    "Mã SK",
                    "Tên sự kiện",
                    "Bắt đầu sự kiện",
                    "Địa điểm",
                    "Mã TNV",
                    "Họ tên (đăng ký)",
                    "Email TNV",
                    "SĐT (đăng ký)",
                    "Trạng thái đăng ký",
                    "Ngày đăng ký",
                    "Lý do / ghi chú"
            };
            writeHeaderRow(detail, 0, detailHeaders, headerStyle);
            int dr = 1;
            int stt = 1;
            for (EventRegistration reg : all) {
                Event ev = reg.getEvent();
                Volunteer v = reg.getVolunteer();
                Row row = detail.createRow(dr++);
                int c = 0;
                row.createCell(c++).setCellValue(stt++);
                row.createCell(c++).setCellValue(ev.getId());
                row.createCell(c++).setCellValue(nvl(ev.getTitle()));
                row.createCell(c++).setCellValue(ev.getStartTime() != null ? DT.format(ev.getStartTime()) : "");
                row.createCell(c++).setCellValue(nvl(ev.getLocation()));
                row.createCell(c++).setCellValue(v.getId());
                row.createCell(c++).setCellValue(nvl(reg.getFullName()));
                row.createCell(c++).setCellValue(nvl(v.getEmail()));
                row.createCell(c++).setCellValue(nvl(reg.getPhone()));
                row.createCell(c++).setCellValue(statusVi(reg.getStatus()));
                row.createCell(c++).setCellValue(reg.getRegisteredDate() != null ? DT.format(reg.getRegisteredDate()) : "");
                row.createCell(c).setCellValue(nvl(reg.getReason()));
            }
            for (int i = 0; i < detailHeaders.length; i++) {
                detail.autoSizeColumn(i);
            }

            // --- Sheet 2: Tổng hợp theo tình nguyện viên ---
            Sheet summary = wb.createSheet("Tổng hợp theo TNV");
            String[] sumHeaders = {
                    "STT", "Mã TNV", "Họ và tên", "Email", "Điện thoại", "Địa chỉ",
                    "Trạng thái sẵn sàng", "Tổng đăng ký", "Đã duyệt", "Từ chối", "Chờ duyệt"
            };
            writeHeaderRow(summary, 0, sumHeaders, headerStyle);
            int sr = 1;
            int sstt = 1;
            for (List<EventRegistration> regs : byVol.values()) {
                Volunteer v = regs.get(0).getVolunteer();
                long conf = regs.stream().filter(r -> "Confirmed".equalsIgnoreCase(r.getStatus())).count();
                long rej = regs.stream().filter(r -> "Rejected".equalsIgnoreCase(r.getStatus())).count();
                long pend = regs.stream().filter(r -> "Pending".equalsIgnoreCase(r.getStatus())).count();

                Row row = summary.createRow(sr++);
                row.createCell(0).setCellValue(sstt++);
                row.createCell(1).setCellValue(v.getId());
                row.createCell(2).setCellValue(nvl(v.getFullName()));
                row.createCell(3).setCellValue(nvl(v.getEmail()));
                row.createCell(4).setCellValue(nvl(v.getPhone()));
                row.createCell(5).setCellValue(nvl(v.getAddress()));
                row.createCell(6).setCellValue(nvl(v.getAvailability()));
                row.createCell(7).setCellValue(regs.size());
                row.createCell(8).setCellValue(conf);
                row.createCell(9).setCellValue(rej);
                row.createCell(10).setCellValue(pend);
            }
            for (int i = 0; i < sumHeaders.length; i++) {
                summary.autoSizeColumn(i);
            }

            wb.write(out);
            return out.toByteArray();
        }
    }

    private static void writeHeaderRow(Sheet sheet, int rowIndex, String[] headers, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(rowIndex);
        for (int i = 0; i < headers.length; i++) {
            Cell c = headerRow.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(headerStyle);
        }
    }

    private static CellStyle createHeaderStyle(Workbook wb) {
        CellStyle headerStyle = wb.createCellStyle();
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerStyle;
    }

    /** Hiển thị tiếng Việt cho cột trạng thái trong Excel. */
    private static String statusVi(String status) {
        if (status == null) return "";
        return switch (status) {
            case "Pending" -> "Chờ duyệt";
            case "Confirmed" -> "Đã duyệt";
            case "Rejected" -> "Từ chối";
            default -> status;
        };
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }
}

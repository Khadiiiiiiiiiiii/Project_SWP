package com.mvc.controller;

import com.mvc.DAO.RevenueReportDAO;
import com.mvc.DAO.StoreDAO;
import com.mvc.dal.DBContext;
import com.mvc.model.RevenueReport;
import com.mvc.model.Store;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@WebServlet("/revenue")
public class RevenueReportController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "list":
                    listReports(request, response);
                    break;
                case "filter":
                    filterReports(request, response);
                    break;
                case "generate":
                    generateReport(request, response);
                    break;
                default:
                    listReports(request, response);
                    break;
            }
        } catch (Exception ex) {
            request.setAttribute("error", "Error: " + ex.getMessage());
            request.getRequestDispatcher("/RevenueReport.jsp").forward(request, response);
        }
    }

    private void listReports(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            RevenueReportDAO reportDAO = new RevenueReportDAO();
            StoreDAO storeDAO = new StoreDAO();

            List<Store> stores = storeDAO.getAllStores();
            request.setAttribute("stores", stores);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date endDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(endDate);
            cal.add(Calendar.DAY_OF_MONTH, -30);
            Date startDate = cal.getTime();

            int selectedStoreId = 0;
            String storeIdParam = request.getParameter("storeId");
            if (storeIdParam != null && !storeIdParam.isEmpty()) {
                selectedStoreId = Integer.parseInt(storeIdParam);
            }

            List<RevenueReport> reports;
            RevenueReport summary;
            if ("generate".equals(request.getParameter("action"))) {
                // Khi nhấn Generate Today's Report, chỉ lấy báo cáo cho ngày hiện tại
                reports = reportDAO.getRevenueReportsByDateRange(endDate, endDate);
                summary = reportDAO.getSummaryByDateRange(endDate, endDate, selectedStoreId);
            } else {
                // Mặc định hoặc filter, lấy theo khoảng thời gian
                reports = reportDAO.getRevenueReportsByDateRange(startDate, endDate);
                summary = reportDAO.getSummaryByDateRange(startDate, endDate, selectedStoreId);
            }

            String formattedStartDate = dateFormat.format(startDate);
            String formattedEndDate = dateFormat.format(endDate);

            request.setAttribute("reports", reports);
            request.setAttribute("summary", summary);
            request.setAttribute("startDate", formattedStartDate);
            request.setAttribute("endDate", formattedEndDate);
            request.setAttribute("selectedStoreId", selectedStoreId);

            request.getRequestDispatcher("/RevenueReport.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error loading reports: " + e.getMessage());
            request.getRequestDispatcher("/RevenueReport.jsp").forward(request, response);
        }
    }

    private void filterReports(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            RevenueReportDAO reportDAO = new RevenueReportDAO();
            StoreDAO storeDAO = new StoreDAO();

            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String storeIdStr = request.getParameter("storeId");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            List<Store> stores = storeDAO.getAllStores();
            request.setAttribute("stores", stores);

            int selectedStoreId = 0;
            if (storeIdStr != null && !storeIdStr.isEmpty() && !storeIdStr.equals("0")) {
                selectedStoreId = Integer.parseInt(storeIdStr);
            }

            List<RevenueReport> reports = reportDAO.getRevenueReportsByDateRange(startDate, endDate);
            RevenueReport summary = reportDAO.getSummaryByDateRange(startDate, endDate, selectedStoreId);

            request.setAttribute("reports", reports);
            request.setAttribute("summary", summary);
            request.setAttribute("startDate", startDateStr);
            request.setAttribute("endDate", endDateStr);
            request.setAttribute("selectedStoreId", selectedStoreId);

            request.getRequestDispatcher("/RevenueReport.jsp").forward(request, response);
        } catch (ParseException e) {
            request.setAttribute("error", "Invalid date format");
            listReports(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            listReports(request, response);
        }
    }

    private void generateReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            RevenueReportDAO reportDAO = new RevenueReportDAO();

            String storeIdStr = request.getParameter("storeId");
            int storeId = 0;
            if (storeIdStr != null && !storeIdStr.isEmpty() && !storeIdStr.equals("0")) {
                storeId = Integer.parseInt(storeIdStr);
            } else {
                StoreDAO storeDAO = new StoreDAO();
                List<Store> stores = storeDAO.getAllStores();
                boolean allSuccess = true;
                for (Store store : stores) {
                    // Xóa dữ liệu cũ trước khi tạo báo cáo cho ngày hôm nay
                    String deleteQuery = "DELETE FROM RevenueReports WHERE report_date = CAST(GETDATE() AS DATE) AND store_id = ?";
                    try ( Connection conn = new DBContext().getConnection();  PreparedStatement psDelete = conn.prepareStatement(deleteQuery)) {
                        psDelete.setInt(1, store.getStoreId());
                        psDelete.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    boolean success = reportDAO.generateTodayReport(store.getStoreId());
                    if (!success) {
                        allSuccess = false;
                        System.out.println("Failed to generate report for Store ID: " + store.getStoreId() + " (No 'completed' orders found)");
                    }
                }
                if (allSuccess) {
                    request.setAttribute("message", "Today's revenue reports have been generated successfully for all stores (completed orders only).");
                } else {
                    request.setAttribute("error", "Failed to generate revenue reports for some stores. No 'completed' orders found for those stores. Check server logs for details.");
                }
                listReports(request, response);
                return;
            }

            // Xóa dữ liệu cũ trước khi tạo báo cáo cho ngày hôm nay
            String deleteQuery = "DELETE FROM RevenueReports WHERE report_date = CAST(GETDATE() AS DATE) AND store_id = ?";
            try ( Connection conn = new DBContext().getConnection();  PreparedStatement psDelete = conn.prepareStatement(deleteQuery)) {
                psDelete.setInt(1, storeId);
                psDelete.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Generate report for the specified store
            boolean success = reportDAO.generateTodayReport(storeId);

            if (success) {
                request.setAttribute("message", "Today's revenue report has been generated successfully for Store ID: " + storeId + " (completed orders only).");
            } else {
                request.setAttribute("error", "Failed to generate revenue report for Store ID: " + storeId + ". No 'completed' orders found for today. Check server logs for details.");
            }

            listReports(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error generating report: " + e.getMessage());
            request.getRequestDispatcher("/RevenueReport.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Revenue Report Controller";
    }
}

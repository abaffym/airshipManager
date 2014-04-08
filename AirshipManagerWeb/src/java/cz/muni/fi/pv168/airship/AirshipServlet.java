/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.airship;

import com.sun.media.jfxmedia.logging.Logger;
import cz.muni.fi.pv168.airshipmanager.Airship;
import cz.muni.fi.pv168.airshipmanager.AirshipManager;
import cz.muni.fi.pv168.airshipmanager.AirshipManagerImpl;
import cz.muni.fi.pv168.common.DBUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author Marek
 */
@WebServlet(AirshipServlet.URL_MAPPING + "/*")
public class AirshipServlet extends HttpServlet {

    private static final String LIST_JSP = "/list.jsp";
    public static final String URL_MAPPING = "/airships";

    
            
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        showAirshipsList(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");

        String action = request.getPathInfo();
        switch (action) {
            case "/add":
                //načtení POST parametrů z formuláře
                String name = request.getParameter("name");
                BigDecimal price = new BigDecimal(request.getParameter("price"));
                int capacity = Integer.valueOf(request.getParameter("capacity"));
                //kontrola vyplnění hodnot
                if (name == null || name.length() == 0 || price == null || 
                        price.compareTo(BigDecimal.ZERO) == 0 || capacity <= 0) {
                    request.setAttribute("chyba", "Je nutné vyplnit všechny hodnoty !");
                    showAirshipsList(request, response);
                    return;
                }
                //zpracování dat - vytvoření záznamu v databázi
                try {
                    Airship airship = new Airship();
                    airship.setName(name);
                    airship.setCapacity(capacity);
                    airship.setPricePerDay(price);
                    airshipManager.addAirship(airship);
//                    log.debug("created {}",airship);
                             //   e.printStackTrace();

                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (Exception e) {
                                System.out.println("aaaaaaaaaa");
                                e.printStackTrace();

            Logger.logMsg(1,"aaaaaaaaa");
//                    log.error("Cannot add airship", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    Airship airship = airshipManager.getAirshipById(id);
                    airshipManager.removeAirship(airship);
//                    log.debug("deleted airship {}",id);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (Exception e) {
//                    log.error("Cannot delete airship", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/update":
//               try {
//                    Long id = Long.valueOf(request.getParameter("id"));
//                    Airship airship = airshipManager.getAirshipById(id);
//                    request.setAttribute("name", airship.getName());
//                    request.setAttribute("capacity", airship.getCapacity());
//                    request.setAttribute("price", airship.getPricePerDay());
//                    airshipManager.editAirship(airship);
//                    
//                } catch (SQLException ex) {
//                    java.util.logging.Logger.getLogger(AirshipServlet.class.getName()).log(Level.SEVERE, null, ex);
//                }
                return;
            default:
//                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    }

    private AirshipManagerImpl airshipManager = new AirshipManagerImpl();
    
    @Resource(name="jdbc/AirshipManager")
    private void setDataSource(DataSource ds) throws SQLException {
        DBUtils.tryCreateTables(ds,AirshipManager.class.getResource("createTables.sql"));
        airshipManager.setDataSource(ds);
    }
    
    /**
     * Stores the list of books to request attribute "books" and forwards to the JSP to display it.
     */
    private void showAirshipsList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("airships", airshipManager.getAllAirships());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cannot show airships");
            Logger.logMsg(1,"Cannot show airships");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}

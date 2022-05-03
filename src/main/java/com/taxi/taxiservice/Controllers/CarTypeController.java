package com.taxi.taxiservice.Controllers;

import com.google.gson.Gson;
import com.taxi.taxiservice.DAO.CarTypeDaoImpl;
import com.taxi.taxiservice.Models.CarType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CarTypeController {
    private CarTypeDaoImpl carTypeDAO;
    Gson gson = new Gson();
    public CarTypeController() {
        carTypeDAO = new CarTypeDaoImpl();
    }

    public void getCarTypes(HttpServletRequest req, HttpServletResponse res) {
        try {
            ArrayList<CarType> carTypes = carTypeDAO.findAll();
            String json = new Gson().toJson(carTypes);
            res.getWriter().write(json);
            res.getWriter().flush();

        } catch (Exception err) {
            MessageController.internal(res);
        }
    }

    public void getCarTypeById(HttpServletRequest req, HttpServletResponse res) {
        String id = req.getPathInfo().substring(1);

        try {
            long typeId = Long.parseLong(id);
            CarType carType = carTypeDAO.findByID(typeId);
            if(carType == null) {
                MessageController.badRequest(res,"Car type not found");
            } else {
                String json = new Gson().toJson(carType);
                res.getWriter().write(json);
            }
            res.getWriter().flush();
        } catch (Exception err) {
            MessageController.internal(res);
        }
    }

    public void getCarTypeByTypename(HttpServletRequest req, HttpServletResponse res) {
        String typename = req.getParameter("type");

        try {
            CarType carType = carTypeDAO.findByTypename(typename);
            if(carType == null) {
                MessageController.badRequest(res,"Car type not found");
            } else {
                String json = new Gson().toJson(carType);
                res.getWriter().write(json);
            }
            res.getWriter().flush();
        } catch (Exception err) {
            MessageController.internal(res);
        }
    }

    public void createCarType(HttpServletRequest req, HttpServletResponse res) {
        try {
            String requestData = req.getReader().lines().collect(Collectors.joining());
            CarType newCarType = gson.fromJson(requestData, CarType.class);
            if(carTypeDAO.findByTypename((newCarType.getTypename())) == null) {
                if(newCarType.checkValid()) {
                    carTypeDAO.save(newCarType.getTypename(), newCarType.getDescription());
                    MessageController.sendResponseMessage(res,"Car type successfully created");
                } else {
                    MessageController.badRequest(res, "Validation failed");
                }
            } else {
                MessageController.badRequest(res, "Car type already exists");
            }
        } catch (Exception err) {
            MessageController.internal(res);
        }
    }

    public void deleteCarTypeById(HttpServletRequest req, HttpServletResponse res) {
        String id = req.getPathInfo().substring(1);

        try {
            long carTypeId = Long.parseLong(id);
            if(carTypeDAO.findByID(carTypeId) != null) {
                carTypeDAO.delete(carTypeId);
                MessageController.sendResponseMessage(res,"Car type deleted successfully");
            } else {
                MessageController.badRequest(res, "Car type doesn`t exist");
            }
            res.getWriter().flush();
        } catch (Exception err) {
            MessageController.internal(res);
        }
    }

    public void updateCarTypeById(HttpServletRequest req, HttpServletResponse res) {
        String id = req.getPathInfo().substring(1);

        try {
            String requestData = req.getReader().lines().collect(Collectors.joining());
            CarType updatedCarType = gson.fromJson(requestData, CarType.class);
            if(updatedCarType.checkValid()) {
                long carTypeId = Long.parseLong(id);
                if(carTypeDAO.findByID(carTypeId) != null) {
                    carTypeDAO.update(carTypeId, updatedCarType.getTypename(), updatedCarType.getDescription());
                    MessageController.sendResponseMessage(res,"Car type updated successfully");
                } else {
                    MessageController.badRequest(res, "Car type doesn`t exist");
                }
            } else {
                MessageController.badRequest(res, "Validation failed");
            }
            res.getWriter().flush();
        } catch (Exception err) {
            MessageController.internal(res);
        }
    }

    public void destroy() {
        carTypeDAO.closeConnection();
    }
}

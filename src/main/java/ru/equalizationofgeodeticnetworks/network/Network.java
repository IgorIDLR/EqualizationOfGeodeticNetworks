package ru.equalizationofgeodeticnetworks.network;

import ru.equalizationofgeodeticnetworks.adjustment.AdjustmentMethod;
import ru.equalizationofgeodeticnetworks.adjustment.RecurrentAdjustment;
import ru.equalizationofgeodeticnetworks.approxStrategy.InitialApproxStrategy;
import ru.equalizationofgeodeticnetworks.approxStrategy.TraverseInitialApprox;
import ru.equalizationofgeodeticnetworks.logger.Logging;
import ru.equalizationofgeodeticnetworks.support.ResultPrinter;

import java.util.Locale;
import java.util.logging.Level;

public class Network {

    private final NetworkData data;
    private final InitialApproxStrategy approxStrategy;
    private AdjustmentMethod adjustmentMethod; // новое поле
    private NetworkSolver solver;

    public Network() {
        this.data = new NetworkData();
        this.approxStrategy = new TraverseInitialApprox();
        this.adjustmentMethod = new RecurrentAdjustment(); // метод по умолчанию
    }

    public Network(AdjustmentMethod adjustmentMethod) {
        this.data = new NetworkData();
        this.approxStrategy = new TraverseInitialApprox();
        this.adjustmentMethod = adjustmentMethod;
    }

    public void setLoggingInfo() {
        Logging.log(Logging.getLogger(Network.class), Level.INFO, String.format(Locale.US,
                "Логгирование информации %s", Logging.isLoggingEnabled() ? "отключено" : "восстановлено"));
        Logging.setLoggingEnabled(!Logging.isLoggingEnabled());
    }


    public ResultPrinter.Builder getConfigurationPrint() { return this.solver.configurePrinter(); }

    public void setApproxStrategy(InitialApproxStrategy strategy) {
        // нельзя изменить после создания solver, но можно пересоздать
        this.solver = null; // заставим пересоздать при следующем solve
    }

    // Методы добавления данных делегируются в data
    public void addFixedPoint(String name, double x, double y) {
        data.addFixedPoint(name, x, y);
    }

    public void addUnknownPoint(String name) {
        data.addUnknownPoint(name);
    }

    public void setStartDir(String pointName, double dirDeg) {
        data.setStartDir(pointName, Math.toRadians(dirDeg));
    }

    public void setEndDir(String pointName, double dirDeg) {
        data.setEndDir(pointName, Math.toRadians(dirDeg));
    }

    public void addAngle(String station, String back, String forward, double valueDeg, double sigmaDeg) {
        data.addAngle(station, back, forward, Math.toRadians(valueDeg), Math.toRadians(sigmaDeg));
    }

    public void addDistance(String p1, String p2, double value, double sigma) {
        data.addDistance(p1, p2, value, sigma);
    }

    public void solve(double eps, int maxIter) {
        if (solver == null) {
            solver = new NetworkSolver(data, approxStrategy, adjustmentMethod);
        }
        solver.solve(eps, maxIter);
    }

    public void setAdjustmentMethod(AdjustmentMethod method) {
        this.adjustmentMethod = method;
        this.solver = null; // сброс кэшированного солвера
    }
}

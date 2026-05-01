package ru.equalizationofgeodeticnetworks.core.adjustment.coreNetwork.coreNetworkImpl;

import lombok.extern.slf4j.Slf4j;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;
import ru.equalizationofgeodeticnetworks.core.adjustment.coreNetwork.AdjustmentEngine;
import ru.equalizationofgeodeticnetworks.model.measurement.Measurement;

import java.util.List;

@Slf4j
public class L1AdjustmentEngine implements AdjustmentEngine {
    @Override
    public void adjust(double[] X, double[][] Q, List<Measurement> measurements, double eps, int maxIter) {
        log.info("L1AdjustmentEngine: минимизация суммы модулей (симплекс-метод)");
        int n = X.length;
        int m = measurements.size();

        ExpressionsBasedModel model = new ExpressionsBasedModel();
        Variable[] params = new Variable[n];
        for (int i = 0; i < n; i++) {
            params[i] = Variable.make("x" + i).lower(Double.NEGATIVE_INFINITY).upper(Double.POSITIVE_INFINITY);
            model.addVariable(params[i]);
        }
        Variable[] u = new Variable[m];
        Variable[] v = new Variable[m];
        for (int i = 0; i < m; i++) {
            u[i] = Variable.make("u" + i).lower(0).upper(Double.POSITIVE_INFINITY);
            v[i] = Variable.make("v" + i).lower(0).upper(Double.POSITIVE_INFINITY);
            model.addVariable(u[i]);
            model.addVariable(v[i]);
        }
        for (int i = 0; i < m; i++) {
            Measurement mm = measurements.get(i);
            double[] deriv = new double[n];
            mm.derivatives(X, deriv);
            double expected = mm.expected(X);
            double residual = mm.getObserved() - expected;
            var expr = model.addExpression("eq" + i);
            for (int j = 0; j < n; j++) {
                expr.setLinearFactor(params[j], deriv[j]);
            }
            expr.setLinearFactor(u[i], -1);
            expr.setLinearFactor(v[i], 1);
            expr.level(residual);
        }
        var objective = model.addExpression("objective");
        for (int i = 0; i < m; i++) {
            objective.setLinearFactor(u[i], 1);
            objective.setLinearFactor(v[i], 1);
        }
        objective.weight(1.0);
        model.setMinimisation();
        Optimisation.Result result = model.minimise();
        if (result.getState().isFeasible()) {
            for (int i = 0; i < n; i++) {
                X[i] = result.getDoubleValue(params[i]);
            }
            // Q не вычисляется, оставляем как есть
        } else {
            log.error("L1-регрессия не дала допустимого решения");
        }
    }
}
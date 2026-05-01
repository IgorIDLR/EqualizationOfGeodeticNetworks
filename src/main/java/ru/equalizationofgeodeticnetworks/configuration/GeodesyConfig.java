package ru.equalizationofgeodeticnetworks.configuration;

import ru.equalizationofgeodeticnetworks.configuration.properties.*;
import ru.equalizationofgeodeticnetworks.core.adjustment.coreNetwork.AdjustmentEngine;
import ru.equalizationofgeodeticnetworks.core.adjustment.coreNetwork.coreNetworkImpl.L1AdjustmentEngine;
import ru.equalizationofgeodeticnetworks.core.adjustment.coreNetwork.coreNetworkImpl.MAdjustmentEngine;
import ru.equalizationofgeodeticnetworks.core.adjustment.coreNetwork.coreNetworkImpl.ParametricAdjustmentEngine;
import ru.equalizationofgeodeticnetworks.core.adjustment.coreNetwork.coreNetworkImpl.RecurrentAdjustmentEngine;
import ru.equalizationofgeodeticnetworks.core.adjustment.freeNetwork.FreeNetworkAdjustment;
import ru.equalizationofgeodeticnetworks.core.adjustment.freeNetwork.freeNetworkImpl.PseudoInverseFreeNetworkSolver;
import ru.equalizationofgeodeticnetworks.core.approx.*;
import ru.equalizationofgeodeticnetworks.core.approx.approxProviderImpl.LevelingApproxProvider;
import ru.equalizationofgeodeticnetworks.core.approx.approxProviderImpl.PolygonometryApproxProvider;
import ru.equalizationofgeodeticnetworks.core.error.*;
import ru.equalizationofgeodeticnetworks.core.graph.GraphNetworkBuilder;
import ru.equalizationofgeodeticnetworks.core.normative.*;
import ru.equalizationofgeodeticnetworks.core.normative.normativeValidatorImpl.GeodesyNormativeValidator;
import ru.equalizationofgeodeticnetworks.core.reliability.ReliabilityAnalyzer;
import ru.equalizationofgeodeticnetworks.core.solver.ConjugateGradientSolver;
import ru.equalizationofgeodeticnetworks.core.solver.ParametricSparseSolver;
import ru.equalizationofgeodeticnetworks.core.statistics.*;
import ru.equalizationofgeodeticnetworks.core.statistics.statisticsCollectorImpl.DefaultStatisticsCollector;
import ru.equalizationofgeodeticnetworks.core.weight.*;
import ru.equalizationofgeodeticnetworks.design.GeneticOptimizer;
import ru.equalizationofgeodeticnetworks.utils.converter.AngleConverter;
import ru.equalizationofgeodeticnetworks.utils.converter.DegreeRadianConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import ru.equalizationofgeodeticnetworks.core.weight.WeightCalculator;

@Configuration
@EnableConfigurationProperties({
        PolygonometryWeightProperties.class,
        LevelingWeightProperties.class,
        RobustAdjustmentProperties.class,
        FreeNetworkProperties.class,
        GraphOptimizationProperties.class,
        SparseSolverProperties.class,
        ConjugateGradientProperties.class,
        ReliabilityProperties.class
})
public class GeodesyConfig {

    @Bean
    @Profile("polygonometry")
    public WeightCalculator polygonometryWeightCalculator(PolygonometryWeightProperties props) {
        return new MixedWeightCalculator(props.getAngleWeight(), props.getSigmaDistUnit());
    }

    @Bean
    @Profile("leveling")
    public WeightCalculator levelingWeightCalculator(LevelingWeightProperties props) {
        return new ConstantWeightCalculator(props.getHeightWeight(), props.getHeightWeight());
    }

    @Bean
    @Primary
    public AdjustmentEngine recurrentAdjustmentEngine(AdjustmentProperties props,
                                                      RobustAdjustmentProperties robustProps) {
        if (props.isUseRobust()) {
            if ("L1".equalsIgnoreCase(props.getRobustMethod())) {
                return new L1AdjustmentEngine();
            } else {
                return new MAdjustmentEngine(props.getRobustMethod(), robustProps);
            }
        }
        return new RecurrentAdjustmentEngine();
    }

    @Bean
    public AdjustmentEngine parametricAdjustmentEngine() {
        return new ParametricAdjustmentEngine();
    }

    @Bean
    @Profile("polygonometry")
    public InitialApproxProvider polygonometryApproxProvider() {
        return new PolygonometryApproxProvider();
    }

    @Bean
    @Profile("leveling")
    public InitialApproxProvider levelingApproxProvider() {
        return new LevelingApproxProvider();
    }

    @Bean
    public ErrorDetector grossErrorDetector() {
        return new GrossErrorDetector();
    }

    @Bean
    public StatisticsCollector defaultStatisticsCollector() {
        return new DefaultStatisticsCollector();
    }

    @Bean
    public NormativeValidator geodesyNormativeValidator() {
        return new GeodesyNormativeValidator();
    }

    @Bean
    public ReliabilityAnalyzer reliabilityAnalyzer() {
        return new ReliabilityAnalyzer();
    }

    @Bean
    public GraphNetworkBuilder graphNetworkBuilder(GraphOptimizationProperties graphProps) {
        return new GraphNetworkBuilder(graphProps.isEnabled());
    }

    @Bean
    public ConjugateGradientSolver conjugateGradientSolver(ConjugateGradientProperties props) {
        return new ConjugateGradientSolver(props.getMaxIter(), props.getTolerance());
    }

    @Bean
    public ParametricSparseSolver parametricSparseSolver() {
        return new ParametricSparseSolver();
    }

    @Bean
    public GeneticOptimizer geneticOptimizer() {
        return new GeneticOptimizer();
    }

    @Bean
    public FreeNetworkAdjustment pseudoInverseFreeNetworkSolver(FreeNetworkProperties freeProps) {
        PseudoInverseFreeNetworkSolver solver = new PseudoInverseFreeNetworkSolver();
        solver.setFixationType(FreeNetworkAdjustment.FixationType.valueOf(freeProps.getFixationType()));
        return solver;
    }

    @Bean
    public AngleConverter angleConverter() {
        return new DegreeRadianConverter();
    }
}

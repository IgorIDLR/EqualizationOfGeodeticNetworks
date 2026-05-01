package ru.equalizationofgeodeticnetworks.core.design;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Random;

@Slf4j
@Component
public class GeneticOptimizer {
    private final Random random = new Random();
    private int populationSize = 100;
    private int generations = 50;
    private double mutationRate = 0.1;
    private double crossoverRate = 0.8;

    public void setPopulationSize(int size) { this.populationSize = size; }
    public void setGenerations(int gens) { this.generations = gens; }
    public void setMutationRate(double rate) { this.mutationRate = rate; }
    public void setCrossoverRate(double rate) { this.crossoverRate = rate; }

    public double[] optimize(NetworkFitnessFunction fitness) {
        int n = fitness.getDimension();
        double[][] population = initializePopulation(n);
        double bestFitness = Double.NEGATIVE_INFINITY;
        double[] bestSolution = null;
        for (int gen = 0; gen < generations; gen++) {
            double[] fitnessValues = new double[populationSize];
            for (int i = 0; i < populationSize; i++) {
                fitnessValues[i] = fitness.evaluate(population[i]);
                if (fitnessValues[i] > bestFitness) {
                    bestFitness = fitnessValues[i];
                    bestSolution = population[i].clone();
                }
            }
            double[][] newPopulation = new double[populationSize][n];
            for (int i = 0; i < populationSize; i++) {
                int p1 = tournamentSelect(fitnessValues);
                int p2 = tournamentSelect(fitnessValues);
                double[] child = crossover(population[p1], population[p2]);
                mutate(child);
                newPopulation[i] = child;
            }
            population = newPopulation;
            log.info("Поколение {}: лучшая приспособленность = {}", gen+1, bestFitness);
        }
        return bestSolution;
    }

    private double[][] initializePopulation(int n) {
        double[][] pop = new double[populationSize][n];
        for (int i = 0; i < populationSize; i++) {
            for (int j = 0; j < n; j++) {
                pop[i][j] = random.nextDouble() * 2 - 1;
            }
        }
        return pop;
    }

    private int tournamentSelect(double[] fitness) {
        int best = random.nextInt(populationSize);
        for (int i = 0; i < 3; i++) {
            int idx = random.nextInt(populationSize);
            if (fitness[idx] > fitness[best]) best = idx;
        }
        return best;
    }

    private double[] crossover(double[] a, double[] b) {
        double[] child = new double[a.length];
        if (random.nextDouble() < crossoverRate) {
            int point = random.nextInt(a.length);
            for (int i = 0; i < a.length; i++) {
                child[i] = (i < point) ? a[i] : b[i];
            }
        } else {
            System.arraycopy(a, 0, child, 0, a.length);
        }
        return child;
    }

    private void mutate(double[] x) {
        for (int i = 0; i < x.length; i++) {
            if (random.nextDouble() < mutationRate) {
                x[i] += (random.nextDouble() - 0.5) * 0.1;
            }
        }
    }

    public interface NetworkFitnessFunction {
        int getDimension();
        double evaluate(double[] parameters);
    }
}

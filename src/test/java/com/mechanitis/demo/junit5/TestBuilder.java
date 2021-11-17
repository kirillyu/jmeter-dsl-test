package com.mechanitis.demo.junit5;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


import us.abstracta.jmeter.javadsl.core.testelements.DslSampler;
import us.abstracta.jmeter.javadsl.core.threadgroups.BaseThreadGroup.ThreadGroupChild;
import us.abstracta.jmeter.javadsl.core.threadgroups.DslThreadGroup;
import us.abstracta.jmeter.javadsl.core.configs.DslCsvDataSet;
import us.abstracta.jmeter.javadsl.core.listeners.InfluxDbBackendListener;
import us.abstracta.jmeter.javadsl.core.listeners.JtlWriter;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;
import us.abstracta.jmeter.javadsl.core.DslTestPlan.TestPlanChild;
import us.abstracta.jmeter.javadsl.core.DslTestPlan;
import us.abstracta.jmeter.javadsl.core.threadgroups.RpsThreadGroup;



public class TestBuilder {
    protected static class RpsFragmentProfile {

        private final int rps;
        private final DslSampler sampler;
        private final List<RpsFragmentProfile> children;
    
        protected RpsFragmentProfile(int rps, DslSampler sampler, RpsFragmentProfile... children) {
            this.rps = rps;
            this.sampler = sampler;
            this.children = Arrays.asList(children);
        }
    
        protected List<ThreadGroupChild> buildTestPlanPartWithBaseRps(int parentRps) {
            List<ThreadGroupChild> ret = new ArrayList<>();
            ret.add(sampler);
            ret.addAll(children.stream()
                .flatMap(c -> c.buildTestPlanPartWithBaseRps(rps).stream())
                .collect(Collectors.toList()));
            if (rps == parentRps) {
                return ret;
            }
            double factor = (double) rps / parentRps;
            ThreadGroupChild[] retArr = ret.toArray(new ThreadGroupChild[0]);
            if (factor > 1) {
                double fraction = factor - Math.floor(factor);
                int loops = (int) Math.ceil(factor);
                if (fraction == 0.0) {
                    return Collections.singletonList(forLoopController(loops,retArr));
                } else {
                    return Collections.singletonList(forLoopController(loops,percentController((float) factor / loops * 100, retArr)));
                }
            } else {
                return Collections.singletonList(percentController((float) factor * 100, retArr));
            }
        }
    }

    protected static class SimpleFragmentProfile {

        private final DslSampler sampler;
        private final List<SimpleFragmentProfile> children;

        protected SimpleFragmentProfile(DslSampler sampler, SimpleFragmentProfile... children) {
            this.sampler = sampler;
            this.children = Arrays.asList(children);
        }
        protected List<ThreadGroupChild> buildTestSimplePlanPart() {
            List<ThreadGroupChild> ret = new ArrayList<>();
            ret.add(sampler);
            ret.addAll(children.stream()
                .flatMap(c -> c.buildTestSimplePlanPart().stream())
                .collect(Collectors.toList()));
                return ret;
        }
    }

    protected static class RpsTestPlanProfile {

        protected final static List<RpsFragmentProfile> fragments = new ArrayList<>();

        protected RpsTestPlanProfile add(int rps, DslSampler sampler,
        RpsFragmentProfile... children) {
            fragments.add(new RpsFragmentProfile(rps, sampler, children));
            return this;
        }
        protected static  List<RpsFragmentProfile> getFragments() {
            return fragments;
        }
        protected  RpsThreadGroup RpsThreadGroupCreate(Function<Integer, RpsThreadGroup> threadGroupBuilder) {
        int maxRps = RpsTestPlanProfile.getFragments().stream().mapToInt(c -> c.rps).max().orElse(0);
        return threadGroupBuilder.apply(maxRps)
          .counting(RpsThreadGroup.EventType.ITERATIONS)
          .children(
            RpsTestPlanProfile.getFragments().stream()
                  .flatMap(r -> r.buildTestPlanPartWithBaseRps(maxRps).stream())
                  .toArray(ThreadGroupChild[]::new)
          );    
        }
    }

    protected static class SimpleTestPlanProfile {

        protected final static List<SimpleFragmentProfile> fragments = new ArrayList<>();

        protected SimpleTestPlanProfile add(DslSampler sampler,
        SimpleFragmentProfile... children) {
            fragments.add(new SimpleFragmentProfile(sampler, children));
            return this;
        }
        protected static  List<SimpleFragmentProfile> getFragments() {
            return fragments;
        }

        protected DslThreadGroup SimpleThreadGroupCreate(Function<Integer,DslThreadGroup> threadGroupBuilder) {
        return  threadGroupBuilder.apply(1)
          .children(
            SimpleTestPlanProfile.getFragments().stream()
                  .flatMap(r -> r.buildTestSimplePlanPart().stream())
                  .toArray(ThreadGroupChild[]::new)
          );    
        }
    }


    protected static class TestPlanHashMap {
        static List<Object> TestPlanElements = new ArrayList<>();
        
        protected static void add(RpsThreadGroup RpsThreadGroup){
            TestPlanElements.add(RpsThreadGroup);
        }

        protected static void add(DslThreadGroup DslThreadGroup){
            TestPlanElements.add(DslThreadGroup);
        }
        
        protected static void add(DslCsvDataSet DslCsvDataSet){
            TestPlanElements.add(DslCsvDataSet);
        }

        protected static void add(JtlWriter jtlWriter){
            TestPlanElements.add(jtlWriter);
        }
        
        protected static void add(InfluxDbBackendListener influxDbListener){
            TestPlanElements.add(influxDbListener);
        }

        protected static DslTestPlan buildTestPlan() {
            return testPlan(
                    TestPlanElements.toArray(new TestPlanChild[0])
            );
        }

        protected static TestPlanStats run(DslTestPlan FinalTestPlan)
            throws IOException {
                return FinalTestPlan.run();
            }
        
        protected static void saveAsJmx(DslTestPlan FinalTestPlan, String Path)
        throws IOException {
            FinalTestPlan.saveAsJmx(Path);
        }
    }
}

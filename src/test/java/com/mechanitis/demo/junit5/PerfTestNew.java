package com.mechanitis.demo.junit5;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;
import java.time.Duration;
import org.junit.jupiter.api.Test;

import us.abstracta.jmeter.javadsl.core.threadgroups.DslThreadGroup;
import us.abstracta.jmeter.javadsl.core.threadgroups.RpsThreadGroup;


public class PerfTestNew {

  private static DslThreadGroup buildSimpleThreadGroup(int threads) {
    return threadGroup(1,1);
  }

  private static RpsThreadGroup buildThreadGroup(int baseRps) {
    return rpsThreadGroup()
        .maxThreads(100)
        .rampToAndHold(0.5 * baseRps, Duration.ofSeconds(10), Duration.ofMinutes(1))
        .rampToAndHold(1 * baseRps, Duration.ofMinutes(1), Duration.ofMinutes(1))
        .rampToAndHold(1.5 * baseRps, Duration.ofMinutes(1), Duration.ofMinutes(1))
        .rampToAndHold(2 * baseRps, Duration.ofMinutes(1), Duration.ofMinutes(1));
  } 

  @Test
  public void test() throws Exception {
    String host = "https://somehost.io";
    TestBuilder.SimpleTestPlanProfile SimpleThreadGroup = new TestBuilder.SimpleTestPlanProfile()
        .add(Samplers.ShowcaseList.get(host)
        .children(
          jsr223PreProcessor(Utils.UtilsGet()),
          jsonExtractor("SHOWCASE_ID", "[0].showcaseId")
        ))
        .add(Samplers.GetShowcaseById.get(host)
        .children(
          jsr223PostProcessor(Utils.showcaseProductGet())
        ));
        
    host = "https://somehost.io";
    TestBuilder.RpsTestPlanProfile RpsThreadGroup = new TestBuilder.RpsTestPlanProfile()
        .add(30, Samplers.ProductSearch.get(host)
            .children(
              jsr223PostProcessor("vars.put('PRODUCT_PAYLOAD',props.get('PRODUCTS_PAYLOAD')); System.out.println(props.get('PRODUCTS_PAYLOAD'))")
            ))
        .add(6, Samplers.ProductDictionary.get(host))
        .add(4, Samplers.ProductBatch.get(host));
    
    
    TestBuilder.TestPlanHashMap.add(csvDataSet("showcase.csv"));
    TestBuilder.TestPlanHashMap.add(SimpleThreadGroup.SimpleThreadGroupCreate(PerfTestNew::buildSimpleThreadGroup));
    TestBuilder.TestPlanHashMap.add(csvDataSet("products.csv"));
    TestBuilder.TestPlanHashMap.add(RpsThreadGroup.RpsThreadGroupCreate(PerfTestNew::buildThreadGroup));
    TestBuilder.TestPlanHashMap.saveAsJmx(TestBuilder.TestPlanHashMap.buildTestPlan(), "test5.jmx");
  }
}

package de.fuberlin.csw.storm.slidingwindow.example_a;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.StormSubmitter;
import de.fuberlin.csw.storm.slidingwindow.SlidingWindowBolt;

/**
 * This example demonstrates how to use SlidingWindowBolt and its friends
 * by simply calculating the rolling sum of the last 20 numbers in a
 * endless series of random integers. To achieve this, we would like to
 * calculate the sum of four patches of 5 elements each in parallel and
 * afterwards add up the partial sums.
 * 
 * @author Malte Rohde <malte.rohde@inf.fu-berlin.de>
 */
public class RollingSumExampleTopology {
	
	public static void main(String[] args) throws Exception {
		final int PARTICIPANTS = 4;
		final int PARTICIPANTS_SIZE = 5;
		// => sliding window size = 20
		
		TopologyBuilder builder = new TopologyBuilder();
		
		builder.setSpout("random_number_spout", new RandomNumberSpout(1000, 100));
		
		// We tell the SlidingWindowBolt to deliver messages to four (!) participants.
		// We cannot set parallelism here.
		builder.setBolt("sw", new SlidingWindowBolt(PARTICIPANTS))
			.allGrouping("random_number_spout");
		
		// Create the Collector first, so we do not have to repeat the loop.
		BoltDeclarer bd = builder.setBolt("collector", new CollectSumsBolt(PARTICIPANTS));
		
		for(int i = 0; i < PARTICIPANTS; i++) {
			
			// Create a RollingSumBolt with 5 elements.
			// No parallelism here, either.
			builder.setBolt("swp" + i, new RollingSumBolt(i, PARTICIPANTS_SIZE))
				.allGrouping("sw");
			
			// Connect the collector.
			bd.allGrouping("swp" + i);
	
		}
		
		Config conf = new Config();
	        conf.setDebug(false);
       		if(args.length > 0) {
			conf.setNumWorkers(8);
			StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
		} else {
			LocalCluster cluster = new LocalCluster();
		        cluster.submitTopology("rolling_sum", conf, builder.createTopology());
	        	Thread.sleep(10000);
		        cluster.shutdown();
		}
	}
	
}

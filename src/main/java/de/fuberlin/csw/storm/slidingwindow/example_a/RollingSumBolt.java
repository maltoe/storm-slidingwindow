package de.fuberlin.csw.storm.slidingwindow.example_a;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import de.fuberlin.csw.storm.slidingwindow.ArraySlidingWindowParticipantBolt;

/**
 * A SlidingWindowParticipant that calculates the sum of its
 * share of numbers.
 */
class RollingSumBolt extends ArraySlidingWindowParticipantBolt {
	public RollingSumBolt(int participant_id, int array_size) {
		super(participant_id, array_size);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("participant_id", "sum"));			
	}

	@Override
	protected void process(Tuple input, BasicOutputCollector collector) {
		// We do not care about all messages.
	}
	
	@Override
	protected void updated(BasicOutputCollector collector) {
		// But when we've been updated, we want to emit the sum.
		int sum = 0;
		for(Tuple t : window_data) {
			// window_data elements can initially be null.
			if(t == null) continue;
			
			sum += t.getIntegerByField("number");
		}
		collector.emit(new Values(new Integer(participant_id), new Integer(sum)));
	}
}

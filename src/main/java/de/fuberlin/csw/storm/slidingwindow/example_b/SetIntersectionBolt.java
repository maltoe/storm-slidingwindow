package de.fuberlin.csw.storm.slidingwindow.example_b;

import java.lang.reflect.Field;
import java.util.HashSet;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import de.fuberlin.csw.storm.slidingwindow.SlidingWindowParticipantBolt;

class SetIntersectionBolt extends SlidingWindowParticipantBolt {

	HashSet<Integer> window_data;
	
	public SetIntersectionBolt(int participant_id) {
		super(participant_id);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("intersection"));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void process(Tuple input, BasicOutputCollector collector) {
		if(window_data != null) {
			HashSet<Integer> data = (HashSet<Integer>) input.getValue(0);
			
			// Calculate intersection.
			
			HashSet<Integer> smaller, larger;
			if(data.size() < window_data.size()) {
				smaller = data;
				larger = window_data;
			} else {
				smaller = window_data;
				larger = data;
			}
			
			HashSet<Integer> intersection = new HashSet<Integer>();
			for(Integer i : smaller) {
				if(larger.contains(i))
					intersection.add(i);
			}
			
			if(!intersection.isEmpty()) {
				System.out.println("Non-empty intersection found: " + intersection);
				collector.emit(new Values(intersection));
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void update(Tuple input, BasicOutputCollector collector) {
		window_data = (HashSet<Integer>) input.getValue(0);		
	}

}

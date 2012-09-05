package de.fuberlin.csw.storm.slidingwindow;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * A simple bolt that envelopes each incoming message in another
 * message and adds an 'update-sliding-window' field. This field
 * contains the index of the array element to be replaced by the
 * current message, as if the sliding window was implemented as
 * a ring buffer backed by an array.
 * 
 * @author Malte Rohde <malte.rohde@inf.fu-berlin.de>
 */
public class SlidingWindowBolt extends BaseBasicBolt {
	
	final private int size;
	private int k;
	
	/**
	 * C'tor.
	 * 
	 * @param size length of the sliding window (in messages)
	 */
	public SlidingWindowBolt(int size) {
		this.size = size;
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		int participant = k++ % size;
		collector.emit(new Values(new Integer(participant), input));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("update-sliding-window", "data"));
	}
}

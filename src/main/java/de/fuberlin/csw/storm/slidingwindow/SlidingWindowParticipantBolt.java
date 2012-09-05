package de.fuberlin.csw.storm.slidingwindow;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

/**
 * A simple bolt that should be used to unwrap the messages sent
 * by SlidingWindowBolt.
 * 
 * @author Malte Rohde <malte.rohde@inf.fu-berlin.de>
 */
public abstract class SlidingWindowParticipantBolt extends BaseBasicBolt {	
	
	final protected int participant_id;
	
	/**
	 * C'tor.
	 * 
	 * @param participant_id the ID of this sliding window participant
	 */
	public SlidingWindowParticipantBolt(int participant_id) {
		this.participant_id = participant_id;
	}
	
	@Override
	final public void execute(Tuple input, BasicOutputCollector collector) {
		Integer id = input.getInteger(0);
		Tuple data = (Tuple) input.getValue(1);
		
		process(data, collector);
		
		if(id == participant_id)
			update(data, collector);
	}
	
	/**
	 * Subclasses must implement this method as a replacement for the
	 * Storm <c>execute</c> method.
	 * 
	 * @param input the unwrapped data
	 * @param collector the collector
	 */
	protected abstract void process(Tuple input, BasicOutputCollector collector);
	
	/**
	 * This method is called when a message containing the same update id as this
	 * participant has arrived, yet after the message has been processed. The collector 
	 * is passed on to this message in case the user wants to emit a message when
	 * the sliding window participant updates.
	 * 
	 * @param input
	 * @param collector 
	 */
	protected abstract void update(Tuple input, BasicOutputCollector collector);
}

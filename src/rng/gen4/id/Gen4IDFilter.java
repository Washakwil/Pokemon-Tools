package rng.gen4.id;

public class Gen4IDFilter {
	
	int tid, sid, tsv;

	public Gen4IDFilter(int tid, int sid, int tsv) {
		this.tid = tid;
		this.sid = sid;
		this.tsv = tsv;
	}
	
	public boolean isAccepted(int tid, int sid) {
		boolean accecptTid = true, acceptSid = true, acceptPid = true;
		if(this.tid != -1) {
			accecptTid = tid == this.tid;
		}
		if(this.sid != -1) {
			acceptSid = sid == this.sid;
		}
		if(tsv != -1) {
			acceptPid = tsv == (tid ^ sid) >>> 3;
		}
		return accecptTid && acceptSid && acceptPid;
	}
	
}

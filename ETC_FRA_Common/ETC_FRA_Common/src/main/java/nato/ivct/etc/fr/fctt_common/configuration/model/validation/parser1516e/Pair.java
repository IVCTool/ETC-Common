package nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e;

public class Pair<X, Y> {
	X first;
	Y second;
	
	public Pair(X x,Y y){
		first=x;
		second=y;
		
	}
	Pair (){
		first=null;
		second=null;
	}
	
	public X getFirst() {
		return first;
	}
	
	public Y getSecond() {
		return second;
	}
	
	void setFirst(X x){
		first=x;
	}
	
	void setSecond(Y y){
		second=y;
	}
}

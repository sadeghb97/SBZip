public class BooleanNode {
    private boolean value;
    private BooleanNode next;
    
    public BooleanNode(boolean val){
        value=val;
        next=null;
    }
    
    public boolean getValue(){
        return value;
    }
    
    public void setValue(boolean val){
        value=val;
    }
    
    public BooleanNode getNext(){
        return next;
    }
    
    public void setNext(BooleanNode nx){
        next=nx;
    }
}

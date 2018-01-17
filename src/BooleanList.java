import java.io.*;

public class BooleanList {
    private BooleanNode root;
    private BooleanNode last;
    private int size;
    
    public BooleanList(BooleanNode bn){
        root=bn;
        if(bn==null)size=0;
        else size=1;
        last =bn;
    }
    
    public BooleanNode getRoot(){
        return root;
    }
    
    public BooleanNode getLast(){
        return last;
    }
    
    public int getSize(){
        return size;
    }
    
    public boolean isEmpty(){
        return root==null;
    }
    
    public BooleanNode InsertFirst(BooleanNode newNode){
        if(newNode==null){
            throw new NullPointerException();
        }
        
        size++;
        if(root==null){
            root=newNode;
            last=newNode;
            return newNode;
        }
        
        newNode.setNext(root);
        root=newNode;
        return newNode;
    }
    
    public BooleanNode InsertAfter(BooleanNode bn,BooleanNode newNode){
        if(bn==null || newNode==null){
            throw new NullPointerException();
        }

        size++;
        if(bn.getNext()!=null) newNode.setNext(bn.getNext());
        bn.setNext(newNode);
        if(newNode.getNext()==null) last=newNode;
        
        return newNode;
    }
    
    public BooleanNode insertLast(BooleanNode newNode){
        if(size>0) return InsertAfter(last, newNode);
        else return InsertFirst(newNode);
    }
    
    @Override
    public String toString(){
        if(isEmpty()) return "NoCode";
        
        String out="";
        BooleanNode bn=root;
        if(bn.getValue()==true) out=out.concat("1");
        else out=out.concat("0");
        
        while(bn.getNext()!=null){
            bn=bn.getNext();
            if(bn.getValue()==true) out=out.concat("1");
            else if(bn.getValue()==false) out=out.concat("0");
        }
        
        return out;
    }
    
    public BooleanList getClone(){
        BooleanList clist=new BooleanList(null);
        if(root==null) return clist;
        clist.InsertFirst(new BooleanNode(root.getValue()));
        
        BooleanNode bn=root;
        while(bn.getNext()!=null){
            bn=bn.getNext();
            clist.insertLast(new BooleanNode(bn.getValue()));
        }
        
        return clist;
    }
    
    public static boolean equalValues(BooleanList l1, BooleanList l2){
        if(l1.getSize()!=l2.getSize()) return false;
        if(l1.getSize()==0 && l2.getSize()==0) return true;
        
        BooleanNode fn1=l1.getRoot();
        BooleanNode fn2=l2.getRoot();
        
        if(fn1.getValue()!=fn2.getValue()) return false;
        
        while(fn1.getNext()!=null){
            if(fn2.getNext()==null) throw new RuntimeException();
            fn1=fn1.getNext();
            fn2=fn2.getNext();
            
            if(fn1.getValue()!=fn2.getValue()) return false;
        }
        
        return true;
    }
    
    public void writeBits(FileOutputStream wr){
        if(size!=8) throw new RuntimeException();
        
        try{
            BooleanNode fn=root;
            int byteValue=0;
            if(fn.getValue()) byteValue=128;
            int zarib=64;
            
            while(fn.getNext()!=null){
                fn=fn.getNext();
                if(fn.getValue()) byteValue+=zarib;
                zarib=zarib/2;
            }
            
            wr.write(byteValue);
            //System.out.println("Writed Byte: "+byteValue);
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
}

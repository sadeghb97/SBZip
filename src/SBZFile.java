import java.io.*;

public class SBZFile {
    private String path;
    private FileInputStream inpStream=null;
    private ByteNode[] byteCodes;
    private int minorBitSize;
    
    public SBZFile(String p) throws IOException{
        try {
            inpStream = new FileInputStream(p);
            path =p;
            byteCodes=new ByteNode[256];
            for(int i=0; 256>i; i++) byteCodes[i]=null;
            minorBitSize=-1;
        }
        catch(FileNotFoundException ex){
            throw ex;
        }
    }
    
    public void createByteCodes(){
        int f[]=new int[256];
        for(int i=0; 256>i; i++) f[i]=0;
        int t;
        boolean haveByte=false;
        try{
            while((t=inpStream.read())!=-1){
                haveByte=true;
                f[t]++;
            }
        }
        catch(Exception ex){}
        minorBitSize=0;
        if(!haveByte) return;
        
        ByteNode bNodes[]=new ByteNode[512];
        int bnLength=0;
        for(int i=0; 256>i; i++){
            if(f[i]>0){
                bNodes[bnLength++]=new ByteNode(i, f[i]);
            }
        }
        
        /*System.out.println("ByteFreqs:");
        for(int i=0; bnLength>i; i++){
            if(bNodes[i].getLabel()>31){
                System.out.println(i+"-"+bNodes[i].getLabel()+"-"+(char)(bNodes[i].getLabel())+": "+bNodes[i].getFreq());
            }
            else{
                System.out.println(i+"-"+bNodes[i].getLabel()+"-\\Control: "+bNodes[i].getFreq());
            }
        }*/
        createHofTree(bNodes,bnLength);
        
        /*System.out.println("\nByteCodes:");
        for(int i=0; 256>i; i++){
            if(byteCodes[i]!=null){
                System.out.println(i+"-"+byteCodes[i].getLabel()+": "+byteCodes[i].getCode().toString());
            }
        }*/
        
        for(int i=0; 256>i; i++){
            if(byteCodes[i]!=null){
                minorBitSize=(minorBitSize+(byteCodes[i].getCode().getSize()*byteCodes[i].getFreq()))%8;
            }
        }
        
        //System.out.println("minorBitSize: "+minorBitSize);
        //System.out.println("L: "+bnLength);
    }
    
    private ByteNode[] getMinFreqByteNodes(ByteNode[] bn,int bnLength){
        ByteNode []out=new ByteNode[2];
        out[0]=null;
        out[1]=null;
        for(int i=0; bnLength>i; i++){
            if(bn[i].isHaveFather()) continue;
            if(out[0]==null){
                out[0]=bn[i];
                continue;
            }
            if(out[1]==null){
                out[1]=bn[i];
                continue;
            }
            
            if(bn[i].getFreq()<out[0].getFreq() && bn[i].getFreq()<out[1].getFreq()){
                if(out[0].getFreq()<out[1].getFreq()){
                    out[1]=bn[i];
                }
                else out[0]=bn[i];
            }
            else if(bn[i].getFreq()<out[0].getFreq()){
                out[0]=bn[i];
            }
            else if(bn[i].getFreq()<out[1].getFreq()){
                out[1]=bn[i];
            }
        }
        
        if(out[1]==null || out[1]==null) return null;
        
        /*System.out.println("Mins: ");
        System.out.print(out[0].getLabel()+"-"+out[0].getFreq()+"  |  ");
        System.out.println(out[1].getLabel()+"-"+out[1].getFreq());*/
        
        if(out[0].getFreq()>out[1].getFreq()){
            ByteNode t=out[0];
            out[0]=out[1];
            out[1]=t;
        }
        return out;
    }
    
    private ByteNode createHofTree(ByteNode[] bn,int bnLength){
        ByteNode mins[]=new ByteNode[2];
        ByteNode lastNode=null;
        while((mins=getMinFreqByteNodes(bn,bnLength))!=null){
            lastNode=new ByteNode(-1, mins[0].getFreq()+mins[1].getFreq());
            lastNode.setLeft(mins[0]);
            lastNode.setRihgt(mins[1]);
            bn[bnLength++]=lastNode;
        }
        
        /*System.out.println("Checking Root: "+lastNode.getFreq());
        System.out.println("Checking Root Left Child: "+lastNode.getLeft().getFreq());
        System.out.println("Checking Root Right Child: "+lastNode.getRight().getFreq());
        
        System.out.println("Checking Root Right Child Left Child: "+lastNode.getRight().getLeft().getFreq());
        System.out.println("Checking Root Right Child Right Child: "+lastNode.getRight().getRight().getFreq());*/
        
        CompleteHofTreeCodes(lastNode);
        return lastNode;
    }
    
    private void CompleteHofTreeCodes(ByteNode hofRoot){
        //System.out.println(hofRoot.getLabel()+":");
        if(hofRoot.isHaveFather()){
            if(hofRoot.getFather().getLeft()==hofRoot){
                hofRoot.setCode(hofRoot.getFather().getCode().getClone());
                if(!hofRoot.getCode().isEmpty()) hofRoot.getCode().insertLast(new BooleanNode(false));
                else hofRoot.getCode().InsertFirst(new BooleanNode(false));
            }
            else{
                hofRoot.setCode(hofRoot.getFather().getCode().getClone());
                if(!hofRoot.getCode().isEmpty()) hofRoot.getCode().insertLast(new BooleanNode(true));
                else hofRoot.getCode().InsertFirst(new BooleanNode(true));
            }
        }
        
        if(hofRoot.getLeft()==null && hofRoot.getRight()==null){
            byteCodes[hofRoot.getLabel()]=hofRoot;
            return;
        }
        
        if(hofRoot.getLeft()!=null){
            CompleteHofTreeCodes(hofRoot.getLeft());
        }
        
        if(hofRoot.getRight()!=null){
            CompleteHofTreeCodes(hofRoot.getRight());
        }
    }
    
    public void createArchive(String p) throws IOException{
        try{
            createByteCodes();
            BufferedWriter out=new BufferedWriter(new FileWriter(p));
            out.write("@SBZ File V1@\n");
            out.write("@MBS:"+minorBitSize+"@\n");
            System.out.println("Header Writed Successfuly!");
            for(int i=0; 256>i; i++){
                if(byteCodes[i]!=null){
                    byteCodes[i].writeCode(out);
                }
            }
            out.write("@HeaderEnd@\n");
            out.close();
        }
        catch(IOException ex){
            throw ex;
        }
        
        try{
            FileOutputStream out=new FileOutputStream(p, true);
            BooleanList cell=new BooleanList(null);
            
            if(minorBitSize>0){
                for(int i=0; 8-minorBitSize>i; i++){
                    cell.insertLast(new BooleanNode(false));
                }
            }
            
            inpStream.getChannel().position(0);
            int t;
            while((t=inpStream.read())!=-1){
                cell=writeByteCode(cell, out, t);
            }
            out.close();
        }
        catch(IOException ex){
            throw ex;
        }
    }
    
    public BooleanList writeByteCode(BooleanList cell, FileOutputStream out, int byteValue){
        BooleanList byteCodeList=byteCodes[byteValue].getCode();
        if(byteCodeList.getSize()==0) return cell;
        if(cell.getSize()>=8) throw new RuntimeException();
        
        BooleanNode fn=byteCodeList.getRoot();
        if(cell.getSize()==0) cell.InsertFirst(new BooleanNode(fn.getValue()));
        else cell.insertLast(new BooleanNode(fn.getValue()));
        if(cell.getSize()==8){
            cell.writeBits(out);
            cell=new BooleanList(null);
        }
        
        while(fn.getNext()!=null){
            fn=fn.getNext();
            if(cell.getSize()==0) cell.InsertFirst(new BooleanNode(fn.getValue()));
            else cell.insertLast(new BooleanNode(fn.getValue()));
            if(cell.getSize()==8){
                cell.writeBits(out);
                cell=new BooleanList(null);
            }
        }
        return cell;
    }
    
    public static boolean extractSBZFile(String srcPath,String dstPath) throws IOException{
        try{
            int minor;
            int byteRead=0;
            BufferedReader rd=new BufferedReader(new FileReader(srcPath));
            String fs=rd.readLine();
            byteRead+=(fs.length()+1);
            if(fs.compareTo("@SBZ File V1@")!=0) return false;

            fs=rd.readLine();
            byteRead+=(fs.length()+1);
            String pieces[]=fs.split(":");
            if(pieces.length<2) return false;
            minor=Integer.valueOf(pieces[1].substring(0,pieces[1].length()-1));
            
            ByteNode codes[]=new ByteNode[256];
            for(int i=0; 256>i; i++) codes[i]=null;
            int cIndex=0;
            
            while((fs=rd.readLine()).compareTo("@HeaderEnd@")!=0){
                byteRead+=(fs.length()+1);
                int byteValue;
                String code;
                
                pieces=fs.split(":");
                String intStr=pieces[0].substring(1);
                byteValue=Integer.valueOf(intStr);
                code=pieces[1].substring(0,pieces[1].length()-1);
                
                codes[cIndex]=new ByteNode(byteValue, 0);
                char bool=code.charAt(0);
                if(bool=='0') codes[cIndex].getCode().InsertFirst(new BooleanNode(false));
                else codes[cIndex].getCode().InsertFirst(new BooleanNode(true));
                
                for(int i=1; code.length()>i; i++){
                    bool=code.charAt(i);
                    if(bool=='0') codes[cIndex].getCode().insertLast(new BooleanNode(false));
                    else codes[cIndex].getCode().insertLast(new BooleanNode(true));
                }
                cIndex++;
            }
            byteRead+=(fs.length()+1);
            //System.out.println("Header Readed: "+byteRead+" byte(s)");
            int numCodes=cIndex;
            rd.close();
            
            FileInputStream inp=new FileInputStream(srcPath);
            inp.getChannel().position(byteRead);
            
            FileOutputStream wr=new FileOutputStream(dstPath);
            
            int cellByte;
            boolean firstByte=true;           
            BooleanList floatCode=new BooleanList(null);
            
            while((cellByte=inp.read())!=-1){
                boolean boolArray[]=byteToBoolArray(cellByte);
                for(int i=0; 8>i; i++){
                    if(firstByte && minor>0){
                        i=8-minor;
                        firstByte=false;
                    }
                    
                    floatCode.insertLast(new BooleanNode(boolArray[i]));
                    int wrByte=getCharByCode(codes, numCodes, floatCode);
                    if(wrByte!=-1){
                        wr.write(wrByte);
                        //System.out.println("Byte Writed: "+wrByte);
                        floatCode=new BooleanList(null);
                    }
                }
            }
            
            inp.close();
            wr.close();
            return true;
        }
        catch(IOException ex){
            throw ex;
        }
    }
    
    private static boolean[] byteToBoolArray(int byteValue){
        boolean out[]=new boolean[8];
        int zarib=128;
        for(int i=0; 8>i; i++){
            if(byteValue>=zarib){
                out[i]=true;
                byteValue-=zarib;
            }
            else{
                out[i]=false;
            }
            zarib/=2;
        }
        return out;
    }
    
    private static int getCharByCode(ByteNode codes[],int cLength,BooleanList floatCode){
        for(int i=0; cLength>i; i++){
            if(BooleanList.equalValues(codes[i].getCode(), floatCode)){
                return codes[i].getLabel();
            }
        }
        return -1;
    }
    
    public static String isSBZArchive(String p){
        try{
            BufferedReader rd=new BufferedReader(new FileReader(p));
            String fs=rd.readLine();
            if(fs.compareTo("@SBZ File V1@")!=0) return "false";

            fs=rd.readLine();
            String pieces[]=fs.split(":");
            if(pieces.length<2) return "false";
            if(pieces[1].length()!=2) return "false";
            
            rd.close();
            return "true";
        }
        catch(IOException ex){
            return ex.getMessage();
        }
    }
}

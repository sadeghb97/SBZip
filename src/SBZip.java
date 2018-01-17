import java.io.*;
import java.util.*;

public class SBZip {
    public static void main(String[] args){
        while(true){
            int choice=menu();
            switch(choice){
                case 1: createArchive(); break;
                case 2: extractArchive(); break;
            }
            if(choice==3) break;
        }
    }
    
    public static int nextInt(){
        Scanner scan=new Scanner(System.in);
        String str=scan.next();
        try{
            int out=Integer.valueOf(str);
            return out;
        }
        catch(NumberFormatException ex){
            return Integer.MIN_VALUE;
        }
    }
    
    public static boolean isInt(int num){
        return !(num==Integer.MIN_VALUE);
    }
    
    public static int choice(int min,int max){
        System.out.print("\nEnter Your Choice: ");
        int c=nextInt();
        while(c<min || c>max){
            if(!isInt(c)) System.out.println("Wrong Input!");
            else if(c<min || c>max){
                System.out.println("Invalid Choice Range!");
                System.out.println("Correct Range: ("+min+" To "+max+")");
            }
            
            System.out.println("Try Aain!");
            System.out.print("\nEnter Your Choice: ");
            c=nextInt();
        }
        return c;
    }
    
    public static int menu(){
        System.out.println("\nMenu:");
        System.out.println("1: Create SBZ Archive");
        System.out.println("2: Extract SBZ Archive");
        System.out.println("3: Exit");
        return choice(1,3);
    }
    
    public static void createArchive(){
        SBZFile sbz=null;
        Scanner scan=new Scanner(System.in);
        System.out.println("\nCreate Archive: ");
        System.out.println("Enter Source FileName: ");
        String srcPath=scan.next();
        try{
            sbz=new SBZFile(srcPath);
            System.out.println("Source FileName Entered Successfuly!\n");
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
            return;
        }
        
        System.out.println("Enter Destination SBZ FileName: ");
        String dstPath=scan.next();
        dstPath=dstPath.concat(".sbz");
        try{
            long startTime=System.currentTimeMillis();
            sbz.createArchive(dstPath);
            long runTime=System.currentTimeMillis()-startTime;
            System.out.println("Archive File Created Successfuly!");
            System.out.println("Archiving Time: "+getPrintTime(runTime));
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
            return;
        }
    }
    
    public static void extractArchive(){
        Scanner scan=new Scanner(System.in);
        System.out.println("\nExtract Archive: ");
        System.out.println("Enter Source FileName: ");
        String srcPath=scan.next();
        String result=SBZFile.isSBZArchive(srcPath);
        if(result=="true"){
            System.out.println("Source SBZ FileName Entered Successfuly!\n");
        }
        else if(result=="false"){
            System.out.println("Wrong Source File Type!");
            return;
        }
        else{
            System.out.println(result);
            return;
        }
        
        System.out.println("Enter Destination FileName: ");
        String dstPath=scan.next();
        
        try{
            long startTime=System.currentTimeMillis();
            SBZFile.extractSBZFile(srcPath, dstPath);
            long runTime=System.currentTimeMillis()-startTime;
            
            System.out.println("SBZ File Extracted Successfully!");
            System.out.println("Extracting Time: "+getPrintTime(runTime));
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
            return;
        }
    }
    
    public static String getPrintTime(long ms){
        double pr=(double)(ms)/1000;
        String out;
        if(ms>999999) out=String.format("%.0fs",pr);
        else if(ms>99999) out=String.format("%.1fs",pr);
        else if(ms>9999) out=String.format("%.2fs",pr);
        else if(ms>999) out=String.format("%.3fs",pr);
        else out=String.format("%dms",ms);
        
        return out;
    }
}

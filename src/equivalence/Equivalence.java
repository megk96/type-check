package equivalence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class FilesUtil {
    public static String readTextFile(String fileName) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(fileName).toAbsolutePath()));
        return content;
    }
}
/**
 * 
 * Creates objects that are compared.
 */
class Type{
    
        String name;
        String type;
        int group;
        int line;
        int length;
        ArrayList<Type> within = new ArrayList<Type>();
/**
 * For Primitive Data Types
 */
    public Type(String fname,String ftype, int fgroup, int fline) {
        name = fname; 
        type = ftype;
        group = fgroup;
        line = fline;
    }
/**
 * For Array Types
 */
     public Type(String fname,String ftype, int fgroup, int fline,int flength) {
        name = fname; 
        type = ftype;
        group = fgroup;
        line = fline;
        length = flength;
    }
/**
 * For Structs and functions
 */
    public Type(String fname, int fgroup, ArrayList<Type> flist)
    {
        name = fname;
        group = fgroup;
        within = flist;
        
    }
        
        
        public String name(){
            return name;
        }
        public String type(){
            return type;
        }
        public int group(){
            return group;
        }
        public ArrayList<Type> within(){
            return within;
        }
        public int line(){
            return line;
        }
        public int length(){
            return length;
        }
              
    }

class TypeCheck
{
	
	
	int basicCheck(Type t1, Type t2)
	{
		String s1 = t1.type();
		String s2 = t2.type();
		if(s1.equals(s2)) return 1;
		return 0;
	}
	int arrayCheck(Type t1, Type t2)
	{
		String s1 = t1.type();
		String s2 = t2.type();
		int l1 = t1.length();
		int l2 = t2.length();
		if(s1.equals(s2))
			if(l1==l2)
				return 1;

		return 0;
		
	}
	int basicLineCheck(Type t1, Type t2)
	{
		String s1 = t1.type();
		String s2 = t2.type();
		int l1 = t1.line();
		int l2 = t2.line();
		if(s1.equals(s2))
			if(l1==l2)
				return 1;

		return 0;
		
	}
}

class CompCheck
{
	TypeCheck b = new TypeCheck();
	int compCheck(Type t1, Type t2)
	{	
		ArrayList<Type> ty1 = t1.within();
		ArrayList<Type> ty2 = t2.within();
		int check = 1;	
		for (int i = 0; i < ty1.size(); i++) {
		Type m1 = ty1.get(i);
		Type m2 = ty2.get(i);
		check = b.basicCheck(m1,m2); 

		if(check==0) return 0;
	        }


		
			
		return 1;
	}

	
	
	
	
}

public class Equivalence {

    /**
     * @param args the command line arguments
     */
    ArrayList<Type> variables = new ArrayList<Type>();
    int n;
    public static void main(String[] args) throws IOException{
        int i,j,k;
       Equivalence equ = new Equivalence();
       equ.input();
       // Comment out Below Line
       //System.out.println("Finding.... " + equ.findInArray("newarr"));
	equ.structuralEquivalence();
	equ.nameEquivalence();
	equ.internalEquivalence();
	
    }
    
    /**
     * Takes in name of variable and returns index
     */
    public int findInArray(String name)
    {
        int i;
        for(i = 0; i< variables.size(); i++){
            if(variables.get(i).name().equals(name)){
                break;
            }
        }
        return i;
    }
    
    public void parse(String str,int line){
        int i,j,k;
        String[] inner = str.split(" ",2);
        String[] commas;
        String[] symbols;
        StringTokenizer tokenize;
        if(!inner[0].equals("struct")&&!inner[0].equals("def")&&!inner[0].equals("array")){
            commas = inner[1].split(",");
            for(i=0; i<commas.length; i++){
                variables.add(new Type(commas[i],inner[0],0,line));
            }
        }
        else if(inner[0].equals("struct")){
            //System.out.println(inner[1]);
            tokenize = new StringTokenizer(inner[1],"{}");
            String name = tokenize.nextToken();
            ArrayList<Type> templist = new ArrayList<Type>();
            //System.out.println(name);
            commas = tokenize.nextToken().split(",");
            
            for(i=0; i<commas.length; i++){
                symbols = commas[i].split(" ");
                templist.add(new Type(symbols[1],symbols[0],1,line));
        }
            variables.add(new Type(name,1,templist));
    }
        else if(inner[0].equals("def")){
            String[] defsplit = inner[1].split(" ",2);
            String returntype = defsplit[0];
            
            tokenize = new StringTokenizer(defsplit[1],"()");
            String name = tokenize.nextToken();
            ArrayList<Type> templist = new ArrayList<Type>();
            //System.out.println(name);
            templist.add(new Type("retof" + name,returntype,2,line));
            commas = tokenize.nextToken().split(",");
            
            for(i=0; i<commas.length; i++){
                symbols = commas[i].split(" ");
                templist.add(new Type(symbols[1],symbols[0],2,line));
        }
            variables.add(new Type(name,2,templist));
            
        }
        else if(inner[0].equals("array")){
            String[] defsplit = inner[1].split(" ",2);
            tokenize = new StringTokenizer(defsplit[1],"[]");
            variables.add(new Type(tokenize.nextToken(),defsplit[0],3,line,Integer.parseInt(tokenize.nextToken())));
        }
    }
    
    public void input() throws IOException
    {
        String input = FilesUtil.readTextFile("code.txt");
        input = input.replaceAll("\n", "");

            int i,j,k;
            String[] lines = input.split(";");
            StringTokenizer inside;
            for(i=0; i<lines.length; i++)
            {        
                parse(lines[i],i); 
            }
	    n = variables.size();
            for(i=0; i< variables.size(); i++)
       {
           // Comment out Below line
           System.out.println(variables.get(i).name() + " " + variables.get(i).type() + " " + variables.get(i).group());
       }
    }

	public void structuralEquivalence()
	{	
		

		int[][] curr = new int[n][n];
		
		
		TypeCheck t = new TypeCheck();
		CompCheck c = new CompCheck();
		
		int i,j;
		for(i=0;i<n;i++)
			for(j=0;j<n;j++)
				curr[i][j] = 11;

		int check;
		

			
			for(i=0;i<n;i++)
				for(j=i;j<n;j++)
					{
						int g1 = variables.get(i).group();
						int g2 = variables.get(j).group();
						if(g1!=g2) {
			
							curr[i][j] = 0;
							continue;

								}
						if(g1==0)
						{
							check = t.basicCheck(variables.get(i),variables.get(j));
							curr[i][j] = check;
					
		
						}
						else if((g1==1)||(g1==2))
						{
							check = c.compCheck(variables.get(i),variables.get(j));
							curr[i][j] = check;
							System.out.println(check);
							
							
						}
						else if(g1==3)
						{
							check = t.arrayCheck(variables.get(i),variables.get(j));
							curr[i][j] = check;
							
						}
						
					}




			
		
	
		System.out.println("The following variables have Structural Equivalence\n");	
		System.out.print("     ");
			for(i=0;i<n;i++)
			{	System.out.print(variables.get(i).name());
				System.out.print("  ");
			}
			System.out.println("\n");
			for(i=0;i<n;i++)
				{	System.out.print(variables.get(i).name());
					System.out.print("  ");
					for(j=0;j<n;j++)
					{
						if(curr[i][j]==1)
						System.out.print("yes  ");
						else if(curr[i][j]==0)
						System.out.print("no   ");
						else System.out.print("     ");
						
					}
					System.out.println("\n");

				}
		
		
		


	}
	public void nameEquivalence()
	{
		int[][] curr = new int[n][n];
		TypeCheck t = new TypeCheck();
		int i,j;
		for(i=0;i<n;i++)
			for(j=0;j<n;j++)
				curr[i][j] = 0;
		int check;
		for(i=0;i<n;i++)
				for(j=i+1;j<n;j++)
					{
						int g1 = variables.get(i).group();
						int g2 = variables.get(j).group();
						if(g1!=g2) {
							curr[i][j] = 0;
							continue;

								}
						if(g1==0)
						{
							check = t.basicLineCheck(variables.get(i),variables.get(j));
							curr[i][j] = check;
		
						}
						
						
					}
		System.out.println("The following variables have Name Equivalence\n");
		for(i=0;i<n;i++)
			{
				for(j=0;j<n;j++)
					if(curr[i][j]==1)
						{
							String n1 = variables.get(i).name();	
							String n2 = variables.get(j).name();	
							System.out.println(n1+ " "+n2+"\n");
						}

			}


	
	}
	public void internalEquivalence()
	{
		int[][] curr = new int[n][n];
		TypeCheck t = new TypeCheck();
		int i,j;
		for(i=0;i<n;i++)
			for(j=0;j<n;j++)
				curr[i][j] = 0;
		int check;
		for(i=0;i<n;i++)
				for(j=i+1;j<n;j++)
					{
						int g1 = variables.get(i).group();
						int g2 = variables.get(j).group();
						if(g1!=g2) {

							curr[i][j] = 0;
							continue;

								}
						if(g1==0)
						{
							check = t.basicCheck(variables.get(i),variables.get(j));
							curr[i][j] = check;
		
						}
						else if(g1==3)
						{
							check = t.basicCheck(variables.get(i),variables.get(j));
							curr[i][j] = check;
							
						}
						
						
					}

		System.out.println("The following variables have Internal Name Equivalence\n");
		for(i=0;i<n;i++)
			{
				for(j=0;j<n;j++)
					if(curr[i][j]==1)
						{
							String n1 = variables.get(i).name();	
							String n2 = variables.get(j).name();	
							System.out.println(n1+ " "+n2+"\n");
						}

			}


	
	}
    
}

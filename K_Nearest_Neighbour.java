import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Scanner;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;


//taking advantage of early datamining assignments to both warm up my java skills and write a program
//that can be easily changed to solve more than one type of assignment in order to catch up also.

public class K_Nearest_Neighbour{


    Datasets d;
    
    K_Nearest_Neighbour(){

       	d = new Datasets( "diagnoses.csv", true);
	System.out.println( d.classifierCompareToInput( 1));
    }

    //this class will read in comma seperated value lists and allow for operations
    //to be performed even if they vary a little bit - this way I can be prepared for
    //data sets and simply concentrate on performing operations on them with few lines
    //of code in future
    public class Datasets{

	//flag tells code wether or not it is dealing
	//with a list of classifiers or a list of integers
	boolean is_classifier;
	int size, q = 0;
	String nextLine[];
	CSVReader reader;
	Boolean isNumAndClass;

	//contains a wrapped data structure whos elements have some
	//functionality to allow operations like compare z y lines
	ArrayList<Data> dataline = new ArrayList<Data>();

	//dataset takes the path to a CSV based data set and initialises an array list
	//of objects customized to fit, first argument is the path to the CSV file the second
	//is true if the data starts with a number and ends with a string classifier
	Datasets( String _CSVdatafile, Boolean _isNumAndClass){

	    //this flag determines wether or not the data starts with an agent number and ends with a string representing a class
	    isNumAndClass = _isNumAndClass;
	    
	    try{

		//reads csv file into filestream
		reader = new CSVReader( new FileReader( _CSVdatafile));

		//loops for each line of data cvs data
		while(( nextLine = reader.readNext()) != null){

		    //creates a new temporary data object to initialise the
		    //data line array with
		    Data tmp = new Data();
		    for( String l: nextLine){

			//adds each substring to data class
			tmp.addString(l);		       
		    }
		
		    //it is numbered and has a class type in the form of a string on the end, add those two values to the list
		    //so as to allow special functionality and aid with K nearest neighbour problems
		    if( _isNumAndClass && dataline.size() > 0){

			//ggets first element in list of data and add that to its
			//itemnumber for extra functionalities
			tmp.setItemNum( Integer.parseInt(tmp.items.get(0)));

			//appends last item on the data list to string containing
			//class type for extra functionalities
		        tmp.setClassString( tmp.items.get( tmp.items.size()-1));
		    }

		    //adds the data class to data line
		    dataline.add( tmp);
		}
	    }catch( IOException e){

		System.out.println("Error loading from file:" + _CSVdatafile);
	    }

	}

	//outputs all the data to the console
	void outputData(){

	    for( Data n: dataline){
		
		System.out.print( n.showitems());
	    }
	}

	//outputs headings of the data types
	void headings(){

	    for( String s: dataline.get(0).items)
		System.out.print( s + " ");

	    System.out.println("");
	}

	//prints out each patient id with values based on args
	void printValuesOf( String _k){

	    int t = 0;
	    for( String s: dataline.get(0).items){

		t++;
		
		if( s.equals(_k)){
     
		    break;
		}
	    }

	    System.out.println(" feature data for " + _k);
	    if( t < dataline.get(0).items.size()){

		for(int d = 1; d < dataline.size()-1; d++){

		    System.out.println("     " +dataline.get(d).getItemString(0) + "   " + dataline.get(d).getItemString(t));
		}
	    }else{

		System.out.println(" arg may be spelled wrong or the data does not have a heading");
	    }
	    
	}     

        //will take data object as an argument when finished and then
	//the second argument will be the k amount of neighbours
	//this function will also take an array of rows not to check
	//it will be managed in another function to tidy it however
	int classifierCompareToInput( int row){


	    Scanner in = new Scanner(System.in);

	    Data tmp = new Data();
	    tmp.items.add( String.valueOf(dataline.size()-1));

	    System.out.println();
	    System.out.println("Input the new patient's 5 symptoms regarding");
    	    System.out.println("Sore Throat, Fever, Swollen Glands, Congestion, Headache");
	    System.out.println("Input Yes or No, ONE PER LINE, CASE SENSITIVE!:");
	    System.out.println();
	
	    for(int j=1;j<=5;j++)
		tmp.items.add( in.nextLine());   
	    
	    int temp = 0;

	    //if both the first and last features of a set are not needed
	    if( isNumAndClass){

		    
		    //loops for the number of features that are relevant
		    for( int i = 1; i < dataline.get(0).items.size()-1; i++){

			if( dataline.get( row).items.get(i).equals( tmp.items.get(i))){

			    
			    temp++;
			}
		    }
	      	}
	    }
	    
	    return temp;
	}

	//compares one existing agent row to another existing agent row for classifiers
	int classifierCompareToRows( int _r1, int _r2){

	    int temp = 0;

	    //if both the first and last features of a set are not needed
	    if( isNumAndClass){
    
		//loops for the number of features that are relevant
		for( int i = 1; i < dataline.get(0).items.size()-1; i++){

		    if( dataline.get( _r1).items.get(i).equals(dataline.get( _r2).items.get(i))){

		
			temp++;
		    }
		}
	    }
	    
	    return temp;
	}


	//////////////////////////////////////////////////////////////////////////////////
	// a class designed to manage multi dimensional array lists of any kind of data //
	//////////////////////////////////////////////////////////////////////////////////
	public class Data{

	    ArrayList<String> items;
	    int itemNumber;
	    String classString;
	    
	    Data(){
		
		items = new ArrayList<String>();
	    }
	   

	    //if the data input starts with a listing number
	    void setItemNum( int _num){

		itemNumber = _num;
	    }
	    
	    //if the data ends with a string classifier
	    void setClassString( String _string){

		classString = _string;
	    }
	    
	    void addString( String _S){

		items.add( _S); 
	    }
			 
	    //simply returns the list of items delimited with "-" to show that they have been
	    //split into seperate variables
	    String showitems(){

		String i = "";
		      
		for( String c : items)
		    i += ( c + " ");

		return i;
	    }

	    //this function returns the string value of the item at position in args n
	    String getItemString( int _n){

		return items.get( _n);
	    }

	    //this function is the same as above but if the function can be returned
	    //as a positive integer then it will be else it will return minus one
	    int getItemInt( int _n){

	        try{

		    return Integer.parseInt( items.get( _n));

		}catch( Exception e){

		    System.out.println( "failed to return item as an integer");
		    return -1;
		}		
	    }
	    
	    //this function will return a boolean of the item at args  n as above
	    //it will only work on the condition the item is a 0 / 1, yes/no or
	    //true / false, it will return default false if this does not match
	    //but will also output an error to the console
	    boolean getItemBool( int _n){

		if( items.get( _n) == "1" || items.get( _n) == "yes" || items.get( _n) == "Yes"|| items.get( _n) == "true" || items.get( _n) == "True" || items.get( _n) == "TRUE"){

		    return true;
		}else if( items.get( _n) == "0" || items.get( _n) == "no" || items.get( _n) == "No" || items.get( _n) == "false" || items.get( _n) == "False" || items.get( _n) == "FALSE"){

		    return false;
		}

		System.out.println( "Warning: item not successfully converted to bool from string");
		return false;
	    }
	}
    }
}

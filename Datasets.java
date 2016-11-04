import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.*;
import java.util.*;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;


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

    //stores weights for classifiers using key value pair method
    ArrayList< Pair> weightlist = new ArrayList< Pair>();

    //the object Data contains an array with data for each feature on it, it also contains methods
    //that will perform some operations on that data, the result an object that contains each row of
    //data and getters, setters and other operations to manipulate that data
    ArrayList<Data> dataline = new ArrayList<Data>();

    //this will be initialised with the test set when preparedata is called
    ArrayList<Data> testset = new ArrayList<Data>();

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

		    //gets first element in list of data and add that to its
		    //item number for extra functionalities
		    tmp.setItemNum( (int) Double.parseDouble(tmp.items.get(0)));

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

	    System.out.println( n.showitems());
	}
	System.out.println("\n");
    }

    //outputs all the data to the console
    void outputTestData(){

	for( Data n: testset){

	    System.out.println( n.showitems());
	}
	System.out.println("\n");
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

		System.out.println("   " +dataline.get(d).getItemString(0) + "   " + dataline.get(d).getItemString(t));
	    }
	}else{

	    System.out.println(" arg may be spelled wrong or the data does not have a heading");
	}
    }

    //this function will rescale the data so it is all within the range 0-1
    void rescaling(){

	//this stores the total number of features of the input data
	int featurenum = dataline.get(0).items.size();

	//loops through each feature or column, first and last are skipped as they are
	//PID and classifier
	for (int f = 1; f < featurenum -1; f++) {

	    //this loop and these integers are for finding the max and min values within that
	    //features column
	    double getMin = 1000, getMax = 0;

	    //first gets minimum and max
	    for (int i = 0; i < dataline.size(); i++) {

		if ( getMin > dataline.get(i).getItemDouble(f)){

		    getMin = dataline.get(i).getItemDouble(f);
		}
		if ( getMax < dataline.get(i).getItemDouble(f)){

		    getMax = dataline.get(i).getItemDouble(f);
		}
	    }
	   
	    //now sets value with x' = x - min(x)/(max(x)-min(x))
	    for (int i = 1; i < dataline.size(); i++) {

		dataline.get(i).setItem( f, Double.toString( (dataline.get(i).getItemDouble(f) - getMin) / (getMax - getMin)));
	    }
	}	
    }

    //this function will scale the data so as to have each feature not overpower the others
    ///CHANGE c TO F FOR FEATURE AND r TO I FOR INDEX
    void standardization(){
       
	//this stores the total number of features of the input data
	int featurenum = dataline.get(0).items.size();

	//work out the standard deviation and mean for each particular input feature
	//the standard deviation is σ = sqrt( ⅟ₙ Σⁿᵢ₌₁ (xᵢ - ̂x)) and so theres a fair bit
	//of processing to perform on the data
	double[] standard_deviation = new double[featurenum];

	//this value will store the mean value of the selected feature
	double[] meanfeatures = new double[featurenum];

	//loop for each feature barring the first feature and the last as that would be the classifier
	//and the patient id number
	for( int c = 1; c < featurenum -1; c++){

	    double temp = 0;

	    //loop for each row and calculate the mean of that one feature
	    for( int r = 1; r < dataline.size(); r++){

		temp += dataline.get(r).getItemDouble(c);
	    }

	    //store  the mean features variable and reset temp
	    meanfeatures[c] = temp/dataline.size();
	    temp = 0;

	    //this time round each member of feature data has its value subtracted by the mean
	    //of those features and is squared
	    for( int r = 1; r < dataline.size(); r++){

		temp += Math.pow( dataline.get(r).getItemDouble(c) - meanfeatures[c], 2);
	    }

	    temp = temp/dataline.size();
	    standard_deviation[c] = Math.sqrt(temp);

	    //now for each value the scaled data id put back onto the array lists
	    //but first using the formula x' = xᵢ - ̂x/σ to each item
	    for( int r = 1; r < dataline.size(); r++){

		double temp2 = ( dataline.get(r).getItemDouble(c) - meanfeatures[c])/standard_deviation[c];
		dataline.get(r).setItem( r, Double.toString(temp2));
	    }
	}
    }



    ///////////////////////////    prepare data     \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\////////////////\\\\\\\\\\\\\\\\
    //this function takes another array of data adds it to the present dataline\\\
    //array list and scales it, it then removes that scaled data from dataline\\\\
    //and initialised the testset array list                                   ///
    //////////////////////////////////////////////////////////////////////////////
    void prepareData( ArrayList<Data> _testset){

	//remove the first line of the test set as it contains headings
	_testset.remove(0);

	int tsize = _testset.size();

	//adds test data to training data and scales it
	dataline.addAll( _testset);

	rescaling();
	//standardization();

	
	//seperates test and training set again, initialising the
	//member object for containing the data set for this class
	for ( int i = dataline.size()-1; i > tsize; i--) {

	    testset.add( dataline.get(i));
	    dataline.remove(i);
	}

	//loops for each item of testing data
	for (int i = 0; i < testset.size(); i++) {

	    //loops for each item of training data to each testing data to find the fit
	    //of each one and storeit in each testing data
	    for( int r = 1; r < dataline.size(); r++){

		testset.get(i).findFit( dataline.get(r));
	    }
	}
    }

    //makes predictions for K nearest outputs a .cvs file and confusion matrix
    //complete with performance indicators, this is the only class that is not
    //in keeping with the object oriented paradigm I have managed to keep consistently
    //the solution will to dynamically initialise an arraylist in the data class
    //and use the prediction and classString to return data for processing
    void findKN( int _K){


	for (int i = 0; i < testset.size(); i++) {

	    testset.get(i).getLowest( _K);
	}

	for (int i = 0; i < testset.size(); i++) {

	    testset.get(i).Weights( _K);
	    //System.out.println(testset.get(i).prediction + " " + testset.get(i).classString);
	    // testset.get(i).bestfits(_K);
	    //System.out.println("\n\n");
	}

	int tMalign = 0, fMalign = 0, fBenign = 0, tBenign = 0;
	double Acc = 0, Spec = 0, Prec = 0, Sen = 0;

	for ( int i = 0; i < testset.size(); i++)
	    if( testset.get(i).classString.equals( "malign") && testset.get(i).prediction.equals( "malign"))
		tMalign++;


	for ( int i = 0; i < testset.size(); i++)
	    if( testset.get(i).classString.equals( "malign") && testset.get(i).prediction.equals( "benign"))
		fMalign++;


	for ( int i = 0; i < testset.size(); i++)
	    if( testset.get(i).classString.equals( "benign") && testset.get(i).prediction.equals( "benign"))
		tBenign++;


	for ( int i = 0; i < testset.size(); i++)
	    if( testset.get(i).classString.equals( "benign") && testset.get(i).prediction.equals( "malign"))
		fBenign++;


	//outputs basic comfusion matrix
	System.out.println( "        _____|Predicted Malign |  Predicted Benign" );
	System.out.println( "Actual Malign|        " + tMalign + "               " + fBenign);
	System.out.println( "Actual Benign|        " + fMalign + "            " + tBenign);

	try{
	    
	    //work out the level of accuracy (TP + TN)/Total
	    Acc = ( tMalign + tBenign)/( testset.size());

	    //precision with respect to 'malign'
	    Prec = tMalign/( tMalign + fMalign);

	    //Specifity with respect to 'malign'
	    Spec = tMalign/( tMalign + fBenign);

	    //sensitivity in respect to 'malign'
	    Sen = tMalign/( tMalign + tBenign);
	}catch(Exception e){

	    System.out.println("Arithmetic exception: values to compute performance identifiers contain zero values ");
	}
	
	System.out.println( "\n\nAccuracy of confusion matrix is: " + Acc + ". Precision with respect to 'malign: " + Prec + ".");
	System.out.println( "Specifity with respect to 'malign: " + Spec + ". Sensitivity with respect to 'malign': " + Sen + ".");
    }


    //outputs to console K nearest with classifier type data and predicts the
    //output, also returns the predicted output as a string
    String classifiersKNearest( int _k, Data _d){


	//loops for each agent initialising each data set with the distance
	for( int row = 0; row < dataline.size(); row++){

	    dataline.get( row).relativepos = classifierCompareRowAndData( row, _d);
	}

	return Weights( _k);
    }

    //takes the total weights of the k nearest classifiers
    //and returns the closest - if they are equal it will return two
    String Weights( int _k){

	sortByNearest();

	//outputs all data once it has sorted ir
	System.out.println( "Nearest data sets are as follows\n");
	outputData();
	System.out.print( " K nearest classifier is: ");

	//this loop adds each value to weightlist in order to eventually add up all
	//nieghbours with same classifiers
	for( int q = 0; q < _k; q++){

	    if(  dataline.get(q).classString == null){

		_k++;
	    }else{

		//first argument in string is the classifier and the second is the computed weight ( 1/1 + distance)
		weightlist.add( new Pair( dataline.get(q).classString, distFraction( dataline.get( q).relativepos)));
	    }
	}

	//checks array for an exact fit - because if there is one then there is no need to use a machine learning algorithm
	for( Pair p: weightlist){
	    if( p.d >= 1){

		System.out.println( "match found");
		return p.s;
	    }
	}


	for( int n = 0; n < _k -1; n++){
	    for( int m = n; m < _k -1; m++){

		if( weightlist.get(n).s.equals(weightlist.get(m).s)){

		    weightlist.get(n).d += weightlist.get(m).d;
		}

	    }
	}

	String temp = "";
	double highest = 0;
	for( Pair p : weightlist){

	    if( p.d > highest){

		temp = p.s;
		highest = p.d;
	    }
	}

	return temp;
    }

    //should be called sort by K nearest
    //sorts the arrayList dataline by the relativePos member value
    //that belongs to the data values - using bubble sort
    void sortByNearest(){

	boolean flag = true;
	Data temp;

	while( flag){

	    flag = false;
	    for( int j = 1; j < dataline.size()-1; j++){

		if( dataline.get(j).relativepos > dataline.get( j+1).relativepos){

		    temp = dataline.get( j);
		    dataline.set( j, dataline.get( j+1));
		    dataline.set( j+1, temp);
		    flag = true;
		}
	    }
	}
    }

    //creates a data object and returns it afterwards
    Data userCreateDataObject(){

	Scanner in = new Scanner(System.in);

	Data d = new Data();

	//adds the next patient number on the end
	d.items.add( String.valueOf(dataline.size()-1));

	System.out.println();
	System.out.println("Input the new patient's data");
	System.out.println(" CASE SENSITIVE!:");
	System.out.println();

	for(int j=1;j<=5;j++)
	    d.items.add( in.nextLine());

	return d;
    }


    //compares a row of data with an object of type data
    int classifierCompareRowAndData(int row, Data data){

	int temp = 0;


	//if both the first and last features of a set are not needed
	if( isNumAndClass){


	    //loops for the number of features that are relevant
	    for( int i = 1; i < dataline.get(0).items.size()-1; i++){

		if( !dataline.get( row).items.get(i).equals( data.items.get(i))){

		    temp++;
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

    double distFraction( double _df){

	double numerator = 1, denominator = 1;
	return numerator / ( denominator + _df );
    }


    ////////////////////////////////////////////////////////////////////////////////////
    ///a class designed to manage a row of data, to be used in an arraylist structure //
    ////////////////////////////////////////////////////////////////////////////////////
    public class Data{

	//stores each item of data
	ArrayList<String> items;

	//stores an array list of K nearest number based training data
	ArrayList<Data> knearest = new ArrayList<Data>();

	ArrayList< Pair> weights = new ArrayList< Pair>();

	//contains the PID or first value as a number to identify the row, classString stores the
	//final classifier at the end
	int itemNumber;
	String classString;

	//contains the prediction based on computing the K nearest
	String prediction;

	//this is for storing this rows position relative to others in relation to the K nearest
	int relativepos;

	//same as relative pos but stores a fitness
	double fitness;

	Data(){

	    items = new ArrayList<String>();
	}

	//outputs n amount of nearest fits - once the fitness have been found that is
	//this is for debugging
	void bestfits( int _n){

	    for (int n = 0; n < _n; n++) {

		int totalfit = 0;

		for (int i = 1; i < items.size()-1; i++) {

		    totalfit += Math.pow( getItemDouble(i) + knearest.get(n).getItemDouble(i), 2);
		}

		System.out.println( "best n fits for are: " + n + " is " + totalfit + " - " + knearest.get(n).classString);
	    }
	}

	//this function takes a Data object and checks its fit against this objects features
	//it then adds the object to knearest array list and updates its fitness value
	void findFit( Data _data){

	    double totalfit = 0;
	    for (int i = 1; i < items.size()-1; i++) {

		totalfit += Math.pow( getItemDouble(i) + _data.getItemDouble(i), 2);
	    }

	    _data.fitness = totalfit;
	    knearest.add( _data);
	}

	//simpler than a sort algorithm, its a find lowest algorithm just with type Data
	//this just reinitialises knearest with the best fitting values starting with the lowest
	void getLowest( int _K){

	    ArrayList<Data>newKnearest = new ArrayList<Data>();

	    //this loops _K times
	    for (int o = 0; o < _K; o++) {

		Data temp = new Data();
		temp.fitness = 1000;

		for (int i = 0; i < knearest.size()-1; i++) {

		    if( knearest.get(i).fitness < temp.fitness){

			temp = knearest.get(i);
		    }
		}

		//remove that lowest value from the list
		removeKnearestByPid( temp.itemNumber);
		newKnearest.add( temp);
	    }

	    //clear knearest array
	    knearest.clear();

	    //update knearest with k sorted values
	    knearest.addAll( newKnearest);
	}

	//this function finds the weighted classifier based on the k nearest value
	//im repeating myself a fair bit here but I may one day refactor all of this
	//long after its been marked
	void Weights( int _K){

	    //adds each classifier and position of k nearest to weights for
	    //finding K value
	    for (int i = 0; i < _K; i++) {

		//adds 1/1+d value to each classifier
		weights.add( new Pair( knearest.get(i).classString, i));
	    }

	    //sums up all classifier chances based on closeness
	    for (int i = 0; i < weights.size(); i++) {
		for (int n = 0; n < weights.size(); n++) {

		    //sums up the total weight of each classifier as it appears
		    if( n != i && weights.get(i).s == weights.get(n).s){

			weights.get(i).d += weights.get(n).d;
		    }
		}
	    }

	    //finds the highest value classifier
	    double highest = 0;
	    for( Pair p : weights){

		if( p.d > highest){

		    prediction = p.s;
		    highest = p.d;
		}
	    }
	}

	//removes item in K nearest with the same item number to clear out repeating rows
	void removeKnearestByPid( int _pid){

	    for (int i = 0; i < knearest.size(); i++) {

		if( knearest.get(i).itemNumber == _pid){

		    knearest.remove(i);
		}
	    }
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
	//as a positive integer else it will return minus one so as to detect an error
	int getItemInt( int _n){

	    try{

		return Integer.parseInt( items.get( _n));

	    }catch( Exception e){

		System.out.println( "failed to return item as an integer");
		return -1;
	    }
	}

	//sets the item at index in args 1 to value in args 2
	//kind of odd converting things to and from string but
	//serves as a good default for oop
	void setItem( int _i, String _s){

	    items.set( _i, _s);
	}

	//this function is the same as above but returns a double
	Double getItemDouble( int _i){

	    try{

		return (double) Double.parseDouble( items.get( _i));

	    }catch( Exception e){

		//	System.out.println( e.toString());
		return 0.0;
	    }
	}

	double distFraction( double _df){

	    double numerator = 1, denominator = 1;
	    return numerator / ( denominator + _df );
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

    //simple helper class stores floats and strings as key value pairs
    class Pair{

	double d;
	String s;

	Pair( String _s, double _d){

	    d = _d;
	    s = _s;
	}
    }
}

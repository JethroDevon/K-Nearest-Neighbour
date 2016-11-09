import java.util.*;

//this class just handles the initialisation of the program and a simple user
//interface
public class K_Nearest_Neighbour{

    Scanner in = new Scanner( System.in);
    Datasets d, train;

    K_Nearest_Neighbour(){

  System.out.println( "please press 1 for task 1 and 2 for task 2, anything else to quit:");

  String c = in.nextLine();

  if( c.equals("1")){

      d = new Datasets( "diagnoses.csv", true);

      int k = 0;

      //makes sure value that is input is not greater than the total
      //number of data
      System.out.println( "Please input value K:");
      boolean flag = true;

      while( flag){

    k = in.nextInt();
    if( k < d.dataline.size()){

        flag = false;
    }
      }
      Datasets.Data data = d.userCreateDataObject();
      System.out.println( d.classifiersKNearest( k, data));

  //option two initialises the data set with the training data, scales it and then
  //tests its accuracy against the test set, following options allow the user to
  //input the k value size and I also intend to compare scaling data with data
  //normalisation as an option
  }else if( c.equals("2")){

      //creates the dataset to work with
      d = new Datasets( "breast_cancer_train.csv", true);

      //initialises a set of data so I can pass it to the dataset object d
      //for processing
      train = new Datasets( "breast_cancer_test.csv", true);

      int k = 0;
      //makes sure value that is input is not greater than the total
      //number of data
      System.out.println( "Please input value K:");
      boolean flag = true;

      while( flag){

    k = in.nextInt();
    if( k < d.dataline.size()){

        flag = false;
    }

    //zeros data and initializes test set
    d.prepareData( train.dataline);

    //uses test set to make predictions, will output a .cvs file based on the K value and display
    //a confusion matrix along with the performance indicators
    d.findKN( k);
      }
  }
    }

    public static void main(String[] args){

        K_Nearest_Neighbour k = new K_Nearest_Neighbour();

    }

}

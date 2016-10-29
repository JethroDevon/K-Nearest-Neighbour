//This code will find the K nearest neighbour with data based on both classiers and
//integers
public class K_Nearest_Neighbour{


    Datasets d;
    
    K_Nearest_Neighbour(){

       	d = new Datasets( "diagnoses.csv", true);
	Datasets.Data data = d.userCreateDataObject();
	System.out.println( d.classifiersKNearest( 3, data));
    }

    public static void main(String[] args){

	K_Nearest_Neighbour k = new K_Nearest_Neighbour();
    }

}

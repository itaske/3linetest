import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Solution1{
    public static void main(String[] args){
        int  array1[] = {1, 5, 7, 4, 1, 2};
        int array2[] = {1, 2, 3};
        int array3[] = {-3,-1};
        System.out.println(smallestPositiveNumberNotFound(array1)); //expected: 3
        System.out.println(smallestPositiveNumberNotFound(array2)); //expected: 4
        System.out.println(smallestPositiveNumberNotFound(array3)); // expected: 1

//        System.out.println(hash("test_20191123132233", "1617953042"));
    }

    /**
     * A function that takes an Integer Array and returns the smallest
     * positive integer not found in the array, Time Complexity of function is O(nlogn)
     * @param array
     * @return smallestPositiveNumber
     */
    public static int smallestPositiveNumberNotFound(int[] array){
        int[] copiedArray = Arrays.copyOf(array, array.length); // copy array to keep values of the original intact

        // use parallel sort to take advantage of multicore as its the most expensive operation
        Arrays.parallelSort(copiedArray);

        copiedArray = Arrays.stream(copiedArray).distinct().filter(num -> num > 0)
                .toArray(); // filter negative numbers and collect only distinct numbers

        int smallestPositiveNumber = 1;

        //count from 1 to the length of the array if any value is missing return it else length+1
        for (int num : copiedArray){
            if (smallestPositiveNumber == num){
                smallestPositiveNumber++;
            }else{
                break;
            }
        }

        return smallestPositiveNumber;
    }


}


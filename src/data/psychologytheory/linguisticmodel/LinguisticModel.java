package data.psychologytheory.linguisticmodel;

/*

====================================================================================================

                          LINGUISTIC MODEL     by Rex Huang (Feb 1, 2024)

====================================================================================================

Prompt: With the given constraints ranking, analyze every syllable of a word from Budai Rukai
        and output the analyzed version of the word.

Input: a word from Budai Rukai

Attributes provided with the input, they are separated by hyphens '-'
 1. Prefix(es)
 2. Indix(es)
 3. Suffix(es)

Expected Output: a parsed version, with the locations of the (primary) stress(es),
                 of the word from the input

Constraints Ranking:
 - WDCOND >> NONFINALITY >> PARSE-σ & WEIGHT-TO-STRESS & ALIGN-HD-R
 - HAVESTRESS/ROOT >> NONFINALITY & TROCHEE >> IAMB
 - FOOTBINARITY >> PARSE-σ >> ALIGN-FT-R >> ALIGN-FT-l
 - WEIGHT-TO-STRESS >> ALIGN-HD-l
 
*/

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class LinguisticModel {
    public Scanner scanner;
    public String input;

    public final List<String> syllables;

    public LinguisticModel() {
        this.scanner = new Scanner(System.in);
        this.syllables = new ArrayList<>();

        this.runProgram();
    }

    public static void main(String[] args) {
        new LinguisticModel();
    }

    private void prompt(String prompt) {
        for (int i = 0; i < 100; i++) {
            System.out.print('=');
        }

        System.out.println("\n\n" + prompt + "\n");

        for (int i = 0; i < 100; i++) {
            System.out.print('=');
        }

        System.out.println();
    }

    private void runProgram() {
        this.prompt("Enter a word from Budai Rukai, separating prefixes, infixes, root, and suffixes!");
        this.input = this.scanner.nextLine();
        this.scanner.close();
        this.prompt("Word: " + this.input);
    }
}

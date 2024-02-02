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
    //Character Array of Vowels, SHOULD NOT BE MODIFIED!!!
    public final char[] vowels = {'i', 'y', 'ɨ', 'ʉ', 'ɯ', 'u', 'ɪ', 'ʏ', 'ʊ', 'e', 'ø', 'ɘ', 'ɵ', 'ɤ', 'o', 'ɛ', 'œ', 'ɜ', 'ɞ', 'ʌ', 'ɔ', 'æ', 'ɐ', 'a', 'ɶ', 'ɑ', 'ɒ'};

    public Scanner scanner;

    public String input;

    public List<String> syllables;
    public List<Integer> sectionStartingIndicies;
    public List<Integer> vowelIndicies;

    public LinguisticModel() {
        this.scanner = new Scanner(System.in);
        this.syllables = new ArrayList<>();
        this.sectionStartingIndicies = new ArrayList<>();
        this.vowelIndicies = new ArrayList<>();

        //Initialize
        this.prompt("Enter a word from Budai Rukai, separating prefixes, infixes, root, and suffixes!");
        this.input = this.scanner.nextLine();
        this.scanner.close();
        this.prompt("Word: " + this.input);

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
        this.interpretInputWord();
    }

    /*
    Interpret the Input
     1. If the word contains '-', indicating different sections, loop through the word and place the starting index of the different sections into an ArrayList. If not, skip this step.
     2. Separate each syllable based on the structure CV or CVG, glide can only be j or w.
    */
    private void interpretInputWord() {
        this.sectionStartingIndicies.add(0);

        for (int index = 0; index < this.input.length(); index++) {
            //Step 1
            if (this.input.charAt(index) == '-') {
                this.sectionStartingIndicies.add(index + 1);
            }

            //Step 2
            for (char character : this.vowels) {
                if (character == this.input.charAt(index)) {
                    this.vowelIndicies.add(index);
                }
            }
        }

        //Step 2
        //TODO: Long vowel not tested
        StringBuilder syllable = new StringBuilder();
        for (int index : this.vowelIndicies) {
            if (index - 1 >= 0) {
                syllable.append(this.input.charAt(index - 1));
            }

            syllable.append(this.input.charAt(index));

            if (index + 1 <= this.input.length() - 1 && (this.input.charAt(index + 1) == 'w' || this.input.charAt(index + 1) == 'j')) {
                syllable.append(this.input.charAt(index + 1));
            }

            this.syllables.add(syllable.toString());
            syllable.setLength(0);
        }

        for (String str : this.syllables) {
            System.out.println(str);
        }
    }
}

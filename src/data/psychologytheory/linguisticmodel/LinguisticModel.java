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
    public final char[] vowels = {'i', 'y', 'ɨ', 'ʉ', 'ɯ', 'u', 'ɪ', 'ʏ', 'ʊ', 'e', 'ø', 'ɘ', 'ɵ', 'ɤ', 'o', 'ə', 'ɛ', 'œ', 'ɜ', 'ɞ', 'ʌ', 'ɔ', 'ɐ', 'æ', 'a', 'ɶ', 'ɑ', 'ɒ'};

    public Scanner scanner;

    public String input;

    public int rootLocation;

    public List<String> syllables;
    public List<Integer> sectionStartingIndicies;
    public List<Integer> vowelIndicies;

    public LinguisticModel() {
        this.scanner = new Scanner(System.in);
        this.rootLocation = 0;
        this.syllables = new ArrayList<>();
        this.sectionStartingIndicies = new ArrayList<>();
        this.vowelIndicies = new ArrayList<>();

        //Initialize
        this.prompt("Enter a word from Budai Rukai, separating prefixes, infixes, root, and suffixes!");
        this.input = this.scanner.nextLine();
        this.scanner.close();

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
        this.parseSyllables();
        this.prompt("Output: " + this.input);
    }

    /*

    Interpret the Input
     1. If the word contains '-', indicating different sections, loop through the word and place the starting index of the different sections into an ArrayList. If not, skip this step.
     3. Separate each syllable based on the structure CV or CVG, glide can only be j or w.

     */
    private void interpretInputWord() {
        this.identifyLocations();
        this.createSyllableList();

        System.out.println("Root starts at: " + this.rootLocation);
        for (String str : this.syllables) {
            System.out.println(str);
        }
    }

    private void identifyLocations() {
        this.sectionStartingIndicies.add(0);

        for (int index = 0; index < this.input.length(); index++) {
            //Step 1: Identify Section Separators
            if (this.input.charAt(index) == '-') {
                this.sectionStartingIndicies.add(index + 1);
            }

            //Step 2: Identify Root Location
            if (this.input.charAt(index) == '[') {
                this.rootLocation = index + 1;
            }

            //Step 3: Identify Vowel Locations
            for (char character : this.vowels) {
                if (character == this.input.charAt(index)) {
                    this.vowelIndicies.add(index);
                }
            }
        }
    }

    private void createSyllableList(){
        //Step 3: Separate Syllables Into ArrayList
        StringBuilder syllable = new StringBuilder();
        for (int index : this.vowelIndicies) {
            //If 2 less of current index is greater or equal to 0 and index - 1 is a section separator and index - 2 is not a vowel or
            //If 2 less of current index is greater or equal to 0 and index - 2 is a section separator
            if ((index - 2 >= 0 && this.input.charAt(index - 1) == '-' && this.isNotVowel(this.input.charAt(index - 2))) || (index - 2 >= 0) && this.input.charAt(index - 2) == '-') {
                syllable.append(this.input.charAt(index - 2));
            }

            //If 1 less of current index is greater or equal to 0 and index - 1 is not a root indicator and index - 2 is not a vowel
            if (index - 1 >= 0 && this.input.charAt(index - 1) != '[' && this.isNotVowel(this.input.charAt(index - 1))) {
                syllable.append(this.input.charAt(index - 1));
            }

            //Vowel
            syllable.append(this.input.charAt(index));

            //Check if the index will be 1 to many | Check if the next character is a ':' or a glide character 'w' or 'j'
            if (index + 1 <= this.input.length() - 1 && (this.input.charAt(index + 1) == ':' || (this.input.charAt(index + 1) == 'w' || this.input.charAt(index + 1) == 'j'))) {
                syllable.append(this.input.charAt(index + 1));
            }

            //Check if the index will be 1 to many | Check if the next character is a glide character 'w' or 'j'
            if (index + 2 <= this.input.length() - 1 && (this.input.charAt(index + 2) == 'w' || this.input.charAt(index + 2) == 'j')) {
                syllable.append(this.input.charAt(index + 2));
            }

            //Add syllable to array list
            this.syllables.add(syllable.toString());

            //Clear StringBuilder
            syllable.setLength(0);
        }
    }

    /*

    Parse the Syllables
     - Based on the constraints ranking, analyze and parse the syllables

     */
    private void parseSyllables() {
        List<String> unparsedSyllables = new ArrayList<>();
        StringBuilder parsedWord = new StringBuilder();
        int currentSyllableIndex = 0;

        //WDCOND
        if (this.syllables.size() == 1) {
            parsedWord.append('(').append('\'').append(this.syllables.get(0)).append(')');
            this.input = parsedWord.toString();
            return;
        }

        //HAVESTRESS/ROOT
        if (this.rootLocation > 0) {
            for (String syllable : this.syllables) {
                if (this.input.charAt(this.rootLocation) == syllable.charAt(0) && this.input.charAt(this.rootLocation + syllable.length() - 1) == syllable.charAt(syllable.length() - 1)) {
                    unparsedSyllables = this.syllables.subList(this.syllables.indexOf(syllable), this.syllables.size());
                    currentSyllableIndex = unparsedSyllables.indexOf(syllable);
                    break;
                }
                parsedWord.append(syllable);
            }
        }

        //HAVESTRESS/ROOT >> NONFINALITY
        if (unparsedSyllables.size() == 1) {
            parsedWord.append('(').append('\'').append(unparsedSyllables.get(0)).append(')');
            this.input = parsedWord.toString();
            return;
        }

        if (currentSyllableIndex + 1 == unparsedSyllables.size() - 1) {
            parsedWord.append("(").append('\'').append(unparsedSyllables.get(currentSyllableIndex)).append(unparsedSyllables.get(currentSyllableIndex + 1)).append(")");
            this.input = parsedWord.toString();
        }

    }

    private boolean isNotVowel(char c) {
        for (char vowel : this.vowels) {
            if (c == vowel) {
                return false;
            }
        }
        return true;
    }
}

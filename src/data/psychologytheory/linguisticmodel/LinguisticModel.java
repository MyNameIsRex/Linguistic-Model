package data.psychologytheory.linguisticmodel;

/*

====================================================================================================

                          LINGUISTIC MODEL     by Rex Huang (Feb 1, 2024 ~ Feb 5, 2024)

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

    public String input;
    public String output;

    public int rootLocation;

    public List<String> syllables;
    public List<Integer> vowelIndicies;

    public LinguisticModel() {
        //Defaulting the root location in the input to 0, or the start of the String
        this.rootLocation = 0;

        //Using ArrayList for easier manipulations
        //Need a syllable list and a vowel indicies list
        this.syllables = new ArrayList<>();
        this.vowelIndicies = new ArrayList<>();
    }

    public void initialize(String input) {
        this.input = input;
        this.output = "";
        this.syllables.clear();
        this.vowelIndicies.clear();
        this.runProgram();
    }

    //Runs the program
    private void runProgram() {
        this.interpretInputWord();
        this.parseSyllables();
    }

    /*

    Interpret the Input
     1. If the word contains '-', indicating different sections, loop through the word and place the starting index of the different sections into an ArrayList. If not, skip this step.
     3. Separate each syllable based on the structure CV or CVG, glide can only be j or w.

     */
    private void interpretInputWord() {
        //Identify the locations of crucial information, namely the vowel indices in the lexical word/morpheme and the root location of the lexical word/morpheme
        this.identifyLocations();

        //Creates the syllable list for parsing
        this.createSyllableList();
    }

    private void identifyLocations() {
        for (int index = 0; index < this.input.length(); index++) {
            //Step 1: Identify Root Location
            if (this.input.charAt(index) == '[') {
                this.rootLocation = index + 1;
            }

            //Step 2: Identify Vowel Locations
            for (char character : this.vowels) {
                if (character == this.input.charAt(index)) {
                    this.vowelIndicies.add(index);
                }
            }
        }
    }

    private void createSyllableList() {
        //Step 3: Separate Syllables Into ArrayList
        /*

        Syllable
        1. Onset
        2. Vowel
        3. Coda

         */
        StringBuilder syllable = new StringBuilder();
        for (int index : this.vowelIndicies) {
            //1. Onset or section separator '-' or root indicator '['

            /*

            Check:
             - If index - 2 is greater or equal to 0 and
                 1. The character at index - 1 is a section separator '-' and the character index is a consonant
                 or
                 2. The character at index - 2 is a section separator '-'
                 or
                 3. The character at index - 2 is a consonant and not a root indicator '[' nor a long vowel indicator ':" and The character at index - 1 is a consonant

             */
            if (index - 2 >= 0 && ((this.input.charAt(index - 1) == '-' && this.isNotVowel(this.input.charAt(index - 2)) ||
                this.input.charAt(index - 2) == '-') ||
                this.isNotVowel(this.input.charAt(index - 2)) && this.input.charAt(index - 2) != '[' && this.input.charAt(index - 2) != ':' && this.isNotVowel(this.input.charAt(index - 1)))) {
                syllable.append(this.input.charAt(index - 2));
            }

            /*

            Check:
             - If index - 1 is greater than equal to 0 and the character at index - 1 is not a root indicator '[' and the character at index - 1 is a consonant

             */
            if (index - 1 >= 0 && this.input.charAt(index - 1) != '[' && this.isNotVowel(this.input.charAt(index - 1))) {
                syllable.append(this.input.charAt(index - 1));
            }

            //2. Vowel
            syllable.append(this.input.charAt(index));

            //3. Coda and long vowel indicator ':'

            /*

            Check:
             - If index + 1 is less than or equal to maximum index of the lexical word/morpheme and
                1. The character at index + 1 is a long vowel indicator ':'
                or
                2. The character at index + 1 is a glide 'j' or 'w'
             */
            if (index + 1 <= this.input.length() - 1 && (this.input.charAt(index + 1) == ':' || (this.input.charAt(index + 1) == 'w' || this.input.charAt(index + 1) == 'j'))) {
                syllable.append(this.input.charAt(index + 1));
            }

            //Add the syllable to the syllable list
            this.syllables.add(syllable.toString());

            //Empty the syllable list
            syllable.setLength(0);
        }
    }

    /*

    Parse the Syllables
     - Based on the constraints ranking, analyze and parse the syllables

     */
    private void parseSyllables() {
        //Build the parsed version of the word with this StringBuilder
        StringBuilder parsedWord = new StringBuilder();

        //Store all the unparsed syllables into this list, will be modified if the input contains a root indicator '[', indicating the presence of a prefix and/or an infix
        List<String> unparsedSyllables = this.syllables;

        //Can and SHOULD store a maximum of one syllable in this String
        String lastSyllable = "";

        //To make sure there is only one head in the word, only allowing one head (') in a prosodic word
        boolean primaryFootParsed = false;

        //HAVESTRESS/ROOT
        if (this.input.contains("[")) {
            parsedWord.append(this.input, 0, this.rootLocation);
            for (String syllable : this.syllables) {
                if (syllable.charAt(0) == this.input.charAt(this.rootLocation) && syllable.charAt(syllable.length() - 1) == this.input.charAt(this.rootLocation + syllable.length() - 1)) {
                    unparsedSyllables = this.syllables.subList(this.syllables.indexOf(syllable), this.syllables.size());
                    break;
                }
            }
        }

        //WDCOND or single syllable in root
        if (unparsedSyllables.size() == 1) {
            parsedWord.append("('").append(unparsedSyllables.get(0)).append(')');
            this.output = parsedWord.toString();
            return;
        }

        if (unparsedSyllables.size() == 2) {
            if (unparsedSyllables.get(0).contains(":")) {
                parsedWord.append("('").append(unparsedSyllables.get(0)).append(')').append(unparsedSyllables.get(1));
                this.output = parsedWord.toString();
                return;
            }

            parsedWord.append("('").append(unparsedSyllables.get(0)).append(unparsedSyllables.get(1)).append(')');
            this.output = parsedWord.toString();
            return;
        }

        //NONFINALITY
        /*

        Loop Through All Syllables
         1. Loop through the syllables backwards
         2. Disregard the last syllable as the size of the syllable list here should be >= 2
         3. If the syllable contains a long vowel or a glide (j, w), parsed into own foot
         4. Otherwise, if the syllable, now be referred to "the current syllable", is light, skip for now and continue. If the syllable before the current syllable is also light,
            parse both syllables into a foot and continue.
         5. Otherwise, if the syllable is the last syllable, is a light syllable, and the next syllable is parsed, leave this syllable unparsed
         6. Finally, add the final syllable and leave it unparsed, regardless if it is heavy.

         */

        //Step 1 and Step 2
        for (int unparsedSyllableIndex = unparsedSyllables.size() - 2; unparsedSyllableIndex >= 0; unparsedSyllableIndex--) {
            //Step 3
            if (!primaryFootParsed && this.isHeavySyllable(unparsedSyllables.get(unparsedSyllableIndex))) {
                parsedWord.insert(this.rootLocation, "('" + unparsedSyllables.get(unparsedSyllableIndex) + ")");
                primaryFootParsed = true;

                //Since Budai Rukai starts to have secondary stress when the number of syllables are greater than or equal to 5, the remaining syllables of a lexical word with only 4 syllables
                //should remain unparsed
                if (unparsedSyllables.size() == 4) {
                    parsedWord.insert(this.rootLocation, unparsedSyllables.get(unparsedSyllableIndex - 2) + unparsedSyllables.get(unparsedSyllableIndex - 1));
                    break;
                }
                continue;
            }

            //Step 4-2, must be processed when lastSyllable is not empty as then we can parse both syllables
            if (!lastSyllable.isEmpty()) {
                if (!primaryFootParsed) {
                    parsedWord.insert(this.rootLocation, "('" + unparsedSyllables.get(unparsedSyllableIndex) + lastSyllable + ")");
                    lastSyllable = "";
                    primaryFootParsed = true;
                    continue;
                }
                parsedWord.insert(this.rootLocation, "(ˌ" + unparsedSyllables.get(unparsedSyllableIndex) + lastSyllable + ")");
                lastSyllable = "";
                continue;
            }

            //Step 4-1, must be processed before Step 5 logically to make sure a new light syllable can be stored here
            lastSyllable = unparsedSyllables.get(unparsedSyllableIndex);

            //Step 5
            if (unparsedSyllableIndex == 0) {
                parsedWord.insert(this.rootLocation, lastSyllable);
            }
        }
        parsedWord.append(this.syllables.get(this.syllables.size() - 1));
        this.output = parsedWord.toString();
    }

    private boolean isNotVowel(char c) {
        //Loop through the vowel list, if the given character is a vowel, return false, else, return true
        for (char vowel : this.vowels) {
            if (c == vowel) {
                return false;
            }
        }
        return true;
    }

    private boolean isHeavySyllable(String syllable) {
        //If the syllable contains a long vowel indicator ':' or a glide 'j' or 'w'
        return syllable.contains(":") || syllable.contains("j") || syllable.contains("w");
    }

    public String getOutput() {
        return this.output;
    }
}

package net.cupofcode.brainFuckBooks;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class BrainFuckUtils {
	public static String interpret(String s, String inputString) {
		// References: https://www.geeksforgeeks.org/brainfuck-interpreter-java/

		CharacterIterator input = new StringCharacterIterator(inputString);
		String output = "";
		
		int ptr = 0; // Data pointer
		int length = 65535;
		int c = 0;
		byte memory[] = new byte[length];

		// Parsing through each character of the code
		for (int i = 0; i < s.length(); i++) {
			switch (s.charAt(i)) {
			case '>':

				// > moves the pointer to the right

				if (ptr == length - 1)// If memory is full
					ptr = 0;// pointer is returned to zero
				else
					ptr++;
				break;

			// < moves the pointer to the left
			case '<':

				if (ptr == 0) // If the pointer reaches zero

					// pointer is returned to rightmost memory
					// position
					ptr = length - 1;
				else
					ptr--;
				break;

			// + increments the value of the memory
			// cell under the pointer
			case '+':
				memory[ptr]++;
				break;

			// - decrements the value of the memory cell
			// under the pointer
			case '-':
				memory[ptr]--;
				break;

			// . outputs the character signified by the
			// cell at the pointer
			case '.':
				output += (char) (memory[ptr]);
				break;

			// , inputs a character and store it in the
			// cell at the pointer
			case ',':

				memory[ptr] = (byte) input.next();
				break;

			// [ jumps past the matching ] if the cell
			// under the pointer is 0
			case '[':

				if (memory[ptr] == 0) {
					i++;
					while (c > 0 || s.charAt(i) != ']') {
						if (s.charAt(i) == '[')
							c++;
						else if (s.charAt(i) == ']')
							c--;
						i++;
					}
				}
				break;

			// ] jumps back to the matching [ if the
			// cell under the pointer is nonzero
			case ']':

				if (memory[ptr] != 0) {
					i--;
					while (c > 0 || s.charAt(i) != '[') {
						if (s.charAt(i) == ']')
							c++;
						else if (s.charAt(i) == '[')
							c--;
						i--;
					}
					i--;
				}
				break;
			}
		}
		return output;
		
	}
}

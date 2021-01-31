package net.cupofcode.brainFuckBooks;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import net.md_5.bungee.api.ChatColor;

public class BrainFuckUtils {
	
	private static long maxOperations = Integer.MAX_VALUE;
	private static int length = 65535;
	private static int maxTasks = -1;

	public static String interpret(String s, String inputString) {
		// Referenced: https://www.geeksforgeeks.org/brainfuck-interpreter-java/

		CharacterIterator input = new StringCharacterIterator(inputString);
		String output = "";

		long operations = 0;
		int ptr = 0; // Data pointer
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
				memory[ptr] = (input.getIndex() < input.getEndIndex()) ? (byte) input.current() : 0;
				input.next();
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
						else
							operations++;
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
						else
							operations++;
						i--;
					}
					i--;
				}
				break;
			}
			operations++;
			if (operations >= maxOperations) {
				output = ChatColor.BOLD + "BrainFuck program took too long to run";
				break;
			}
			if (output.length() > 256) {
				output = ChatColor.BOLD + "BrainFuck program output is too large";
				break;
			}
		}
		System.out.println(operations);
		return output;
	}

	public static int getRunningBrainFuckTasksCount() {
		int count = 0;
		for (BukkitTask task : Bukkit.getScheduler().getPendingTasks()) {
			if (task.getOwner().equals(BrainFuckBooks.getInstance())) {
				count++;
			}
		}
//		for (BukkitWorker worker : Bukkit.getScheduler().getActiveWorkers()) {
//			if (worker.getOwner().equals(BrainFuckBooks.getInstance())) {
//				count++;
//			}
//		}
		return count;
	}
	
	public static void runBrainFuck(Player p, String code, String input) {
		//	set maxTasks if necessary
		if (maxTasks == -1) {
			maxTasks = (int) BrainFuckBooks.getInstance().getConfig().get("settings.brainfuckbook.program.maxAtOnce");
		}
		
		// check if too may tasks are already running
		if (getRunningBrainFuckTasksCount() >= maxTasks) {
			p.sendMessage(ChatColor.BLUE  + (ChatColor.BOLD + "Too many BrainFuck programs are running at once.  Try again later"));
			return;
		}
		
		Bukkit.getScheduler().runTaskLaterAsynchronously(BrainFuckBooks.getInstance(), new Runnable() {
			@Override
			public void run() {
				// no input
				p.sendMessage(ChatColor.BLUE + BrainFuckUtils.interpret(code, input));
			}
		}, 0);
	}
}

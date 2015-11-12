import java.io.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class RC4Cracker {
	static int keySize;
	static int tuples;
	static ArrayList<int[]> tup  = new ArrayList<int[]>();
	static int[] keys;

	public static void main(String[] args) throws IOException {
		File file = new File("");
		Path path = file.toPath();
		byte[] data = Files.readAllBytes(path);
		ByteBuffer buffer = ByteBuffer.wrap(data);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		keySize = buffer.getInt();
		tuples = buffer.getInt();
		ArrayList<int[]> freq = new ArrayList<int[]>();
		int[] ans = new int[keySize];
		for(int a = 0; a < keySize; a++){
			freq.add(new int[160]);
		}
		for (int i = 0; i < tuples; i++) {
			int[] now = new int[4];
			for (int j = 0; j < 4; j++) {
				now[j] = buffer.get() & 0xFF;
			}
			tup.add(now);
		}
		for(int q = 3; q < keySize; q++){
			for(int a = 0; a < tuples; a++){
				int[] current = tup.get(a);
				int[] setup = new int[160];
				for(int k = 0; k < 160; k++){
					setup[k] = k;
				}

				int[] j = new int[keySize];
				ans[0] = current[0];
				ans[1] = current[1];
				ans[2] = current[2];	
				j[0] = current[0];
				for(int b = 0; b < (q + 1); b++){
					if(b < q){
						int temp = setup[b];
						setup[b] = j[b];
						j[b] = temp;
						if(b < (q - 1)){
							j[b+1] = j[b] + setup[b+1] + ans[b+1];
						}
					}
					else{
						int x0y0 = (setup[1] + setup[setup[1] % 160]) % 160;
						if(x0y0 == b){
							int[] currentFreq = freq.get(b);
							for(int c = 0; c < 160; c++){
								if(setup[c] == current[3]){
									int pos = (c - j[b - 1] - setup[b]) % 160;
									while(pos < 0){
										pos += 160;
									}
									if (pos < 90){
										currentFreq[pos]++;
										freq.set(b, currentFreq);
									}
								}
							}
						}
					}
				}
			}
			int[] ansFreq = freq.get(q);
			int max4 = -3;
			int max3 = -2;
			int max2 = -1;
			int max1 = -1;
			int most1 = -1;
			int most2 = -1;
			int most3 = -1;
			int most4 = -1;
			for(int r = 0; r < 160; r++){
				if (ansFreq[r] >= max1){
					max4 = max3;
					max3 = max2;
					max2 = max1;
					max1 = ansFreq[r];
					most4 = most3;
					most3 = most2;
					most2 = most1;
					most1 = r;
				}
			}
			ans[q] = most1;
		}


		for(int s = 3; s < keySize; s++){
			System.out.print(ans[s] + " ");
		}
		//System.out.println(keySize);
		//System.out.println(tuples);
		//System.out.println(tup.size());
	}
}


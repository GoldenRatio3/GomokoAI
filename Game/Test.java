class Test {
	public static void main(String[] args) {
		char[][] matrix = new char[8][8];
		char ch = 'A';

		for(int i=0; i<8; i++)
			for(int j=0; j<8; j++, ch++)
				matrix[i][j] = ch;

		printMatrix(matrix);

		for( int k = 4 ; k < 8 ; k++ ) {
        	for( int j = 0 ; j <= k ; j++ ) {
            	int i = k - j;
            	System.out.print( matrix[i][j] + " " );
       		 }
        	System.out.println();
   		 }		

	}

	static void printMatrix(char[][] matrix) {
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				System.out.print(matrix[i][j]);
			}
			System.out.println();
		}
	}

	static void printBottomLeftToTopRight(char[][] matrix) {
		for( int k = 4 ; k < 8 ; k++ ) {
        	for( int j = 0 ; j <= k ; j++ ) {
            	int i = k - j;
            	System.out.print( matrix[i][j] + " " );
       		 }
        	System.out.println();
   		 }

   		for( int k = 8 - 2 ; k >= 4 ; k-- ) {
        	for( int j = 0 ; j <= k ; j++ ) {
            	int i = k - j;
            	System.out.print( matrix[8 - j - 1][8 - i - 1] + " " );
        	}
        	System.out.println();
    	}
	}


}
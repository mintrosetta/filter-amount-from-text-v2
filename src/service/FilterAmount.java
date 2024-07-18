package service;

public class FilterAmount {
	private int increasePoint = 1;
	private int decreasePoint = 1;
	private int spacialDecreasePoint = 10;
	private int spacialIncreasePoint = 10;

	public double filter(String text) {
		StringBuilder strBuilder = new StringBuilder();
		boolean isActiveCollect = false; // for control state open and close collect character
		boolean spacialDecrease = false; // for control state spacial decrease point
		boolean spacialIncrease = false; // for control state spacial increase point

		int currentPoint = 0;
		double currentAmount = 0;

		for (int index = 0; index < text.length(); index++) {
			char character = text.charAt(index);

			// if not active collect, check current can open collect
			if (!isActiveCollect) {
				if (this.isNumber(character))
					isActiveCollect = true; // if current character is number, open for collect character

				if (this.isDot(character)) { // if current character is dot
					if ((index + 1) < text.length() - 1) { // check next index is not last index
						char nextCharacter = text.charAt((index + 1)); // get next character from next index [][.][x]
						if (this.isNumber(nextCharacter)) { // if next character is number, open for collect (data will
															// like .24, .78 this is a amount like 0.24, 0.78)
							isActiveCollect = true; // open collect character
						}
					}

					if (index > 0) { // if this character is dot and not index start
						char previosCharacter = text.charAt((index - 1)); // get previos character [x][.][]

						// if is not thai character , will open for collect
						/*
						 * Why thai character not collect ? Because it may be a number for something
						 * else, such as a document number. Example ส.12939
						 */
						if (!this.isThaiCharacter(previosCharacter)) {
							isActiveCollect = true; // open collect character
						} else {
							isActiveCollect = false; // close collect character
						}
					}
				}
			}

			// check current charater is a english charater, because it have condition for
			// spaial decrease point
			if (this.isEngCharacter(Character.toLowerCase(character))) {
				if (Character.toLowerCase(character) == 'i' && (index + 1) < text.length()) { // if character equal 'i'
																								// and next index is not
																								// last length
					char nextCheracter = text.charAt(index + 1); // get next character from next index [][i][x]

					// if next character is equal 'd', next amount will be get spacial decrease
					// point
					/*
					 * Why character 'i' that are followed by 'd' get a spacial decrease point
					 * because it might a SaleId like 29302393, it not a amount !
					 */
					if (Character.toLowerCase(nextCheracter) == 'd') {
						spacialDecrease = true;
					}
				}
			}

			// check current character is a Thai character, because it have condition for
			// spacial increase point
			if (this.isThaiCharacter(character)) {
				if (character == 'ห' && (index + 2) < text.length() - 1) { // if character 'ห' is not last index + 2,
																			// Why +2 ? because หัก concat bwtween [ห,
																			// ั, ก]
					char nextCharacter = text.charAt(index + 1);
					char furtureCharacter = text.charAt(index + 2);

					if (nextCharacter == 'ั' && furtureCharacter == 'ก') { // if next index is equals 'ั' and furture
																			// index is equals 'ก', next number have
																			// high chance to be amount
						spacialIncrease = true; // add spacial increase point for next number
					}
				}
			}

			if (this.isWhiteSpace(character) || this.isThaiCharacter(character) || index == text.length() - 1) {
				if (!isActiveCollect)
					continue;

				// check last index have a number, append to builder
				// if not check at last index, last number will not append and data incorrect
				if (index == text.length() - 1 && this.isNumber(character))
					strBuilder.append(character);

				int tempPoint = 0; // assign point count to 0
				boolean isDuplicatedDot = false; // for validate duplicate dot

				// loop for check condition to increase and decrease
				for (int builderIndex = 0; builderIndex < strBuilder.length(); builderIndex++) {
					char builderCharacter = strBuilder.charAt(builderIndex);

					// if current character is not number, decrease point
					if (!this.isNumber(builderCharacter) && !this.isDot(builderCharacter) && !this.isComma(builderCharacter))
						tempPoint -= this.decreasePoint;

					// if current character is a blackslash
					if (this.isBlackSlash(builderCharacter)) {
						if (spacialIncrease) { // if spacialIncrease is true, deduct point with spacialDecreasePoint
							tempPoint -= this.spacialDecreasePoint;
							spacialIncrease = false; // set spacial increase to false for not added point
						} else {
							tempPoint -= this.increasePoint;
						}
					}
						

					// if number start with 0 and length of builder equal 10, It might be a phone
					// number, decrease point
					if (builderIndex == 0 && builderCharacter == '0' && strBuilder.length() == 10)
						tempPoint -= this.decreasePoint;

					// if current character is dot and not duplicated, increase point
					if (this.isDot(builderCharacter) && !isDuplicatedDot) {
						tempPoint += this.increasePoint;
						isDuplicatedDot = true;
					}
				}

				if (spacialDecrease) {
					tempPoint -= this.spacialDecreasePoint;
					spacialDecrease = false;
				}

				if (spacialIncrease) {
					tempPoint += this.spacialIncreasePoint;
					spacialIncrease = false;
				}

				// if temp point more than current, set temp to current
				if (tempPoint >= currentPoint) {
					currentPoint = tempPoint;
					currentAmount = Double.parseDouble(this.formatTextAmount(strBuilder.toString()));
				}

				strBuilder.setLength(0); // reset string builder
				isActiveCollect = false; // close collect character
			}

			if (isActiveCollect)
				strBuilder.append(character);
		}

		return currentAmount;
	}

	private boolean isNumber(char character) {
		return character >= '0' && character <= '9';
	}

	private boolean isWhiteSpace(char character) {
		return character == ' ';
	}

	private boolean isThaiCharacter(char character) {
		return character >= 'ก' && character <= 'ฮ';
	}

	private boolean isDot(char character) {
		return character == '.';
	}

	private boolean isComma(char character) {
		return character == ',';
	}

	private boolean isEngCharacter(char character) {
		return character >= 'a' && character <= 'z';
	}

	private boolean isBlackSlash(char character) {
		return character == '/';
	}

	private String formatTextAmount(String text) {
		StringBuilder strBuilder = new StringBuilder();

		boolean isDuplicatedDot = false; // for validate duplicated dot

		for (int index = 0; index < text.length(); index++) {
			char c = text.charAt(index);

			// validate dot is duplicated
			if (this.isDot(c)) {
				if (isDuplicatedDot)
					continue; // duplicated

				// not duplicated, set isDuplicatedDot to true for next dot will not collect
				isDuplicatedDot = true;
			}

			if (this.isComma(c))
				continue;

			// store character
			strBuilder.append(c);
		}

		return strBuilder.toString();
	}
}

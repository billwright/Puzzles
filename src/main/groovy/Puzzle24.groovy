//List<Float> puzzleNumbers = [1,2,3,4]
//List<Float> puzzleNumbers = [2,3,4,6]
//List<Float> puzzleNumbers = [2,2,4,8]
List<Float> puzzleNumbers = [3, 3, 8, 8, 2]


class Solver {

    static operations = ['+', '-', '*', '/']

    def solve1(puzzleNumbers) {
        puzzleNumbers.eachWithIndex { firstNumber, firstIndex ->
            def remainingNumbers = puzzleNumbers.clone()
            remainingNumbers.removeAt(firstIndex)
            operations.each { op ->
                remainingNumbers.eachWithIndex { operand, secondIndex ->
                    def expressionOne = calculate(firstNumber, operand, op)
                    String stepOne = "${firstNumber} ${op} ${operand} = ${expressionOne}"

                    def expressionTwoInputs = remainingNumbers.clone()
                    expressionTwoInputs.removeAt(secondIndex)
                    expressionTwoInputs << expressionOne

                    expressionTwoInputs.eachWithIndex { expTwoFirstNumber, thirdIndex ->
                        def remainingNumbersForExp2 = expressionTwoInputs.clone()
                        remainingNumbersForExp2.removeAt(thirdIndex)
                        operations.each { expTwoOp ->
                            remainingNumbersForExp2.eachWithIndex { expTwoSecondNumber, fourthIndex ->
                                def expressionTwo = calculate(expTwoFirstNumber, expTwoSecondNumber, expTwoOp)
                                String stepTwo = "${expTwoFirstNumber} ${expTwoOp} ${expTwoSecondNumber} = ${expressionTwo}"

                                def remainingNumbersForExp3 = remainingNumbersForExp2.clone()
                                remainingNumbersForExp3.removeAt(fourthIndex)
                                remainingNumbersForExp3 << expressionTwo

                                remainingNumbersForExp3.eachWithIndex { expThreeFirstNumber, fifthIndex ->
                                    operations.each { expThreeOp ->
                                        def lastOperands = remainingNumbersForExp3.clone()
                                        lastOperands.removeAt(fifthIndex)
                                        def lastOperand = lastOperands.first()

                                        def expressionThree = calculate(expThreeFirstNumber, lastOperand, expThreeOp)
                                        String stepThree = "${expThreeFirstNumber} ${expThreeOp} ${lastOperand} = ${expressionThree}"
                                        if (Math.abs(expressionThree - 24) < 0.1) {
                                            println "Solved it! Answer is:"
                                            println stepOne
                                            println stepTwo
                                            println stepThree
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Float calculate(operand1, operand2, operation) {
        switch (operation) {
            case '+':
                return operand1 + operand2
                break
            case '-':
                return operand1 - operand2
                break
            case '*':
                return operand1 * operand2
                break
            case '/':
                return operand1 / operand2
                break
            default:
                println "Error! I don't know how to handle operation: ${op}"
        }
    }

    OperandList solve2(puzzleNumbers) {
        OperandList initialList = OperandList.newFromIntegers(puzzleNumbers)
        OperandList solutions = new OperandList()

        generateValues(initialList).items.each { IValue currentCandidate ->
            if (currentCandidate.equals24()) {
//                println "Solution found! It was: \n\t${currentCandidate}"
                solutions.add(currentCandidate)
            }
        }
        return solutions
    }

    OperandList generateValues(OperandList values) {
        if (values.size() == 2) {
            return generateValues(values.first(), values.last())
        }

        OperandList newOperands = new OperandList()
        values.items.eachWithIndex { firstOperand, index ->
            OperandList remainingOperands = generateValues(values.removeAt(index))
            remainingOperands.items.each { secondOperand ->
                newOperands.add(generateValues(firstOperand, secondOperand))
            }
        }
        return newOperands
    }


    // Return the six possible step values for combining these two step values
    OperandList generateValues(IValue operand1, IValue operand2) {
        OperandList newValues = new OperandList()
        operations.each {
            if (it == '+' || it == '*') {
                newValues.add(new OperationValue(operand1, operand2, it))
            } else {
                newValues.add(new OperationValue(operand1, operand2, it))
                if (operand1.result() != operand2.result()) {
                    newValues.add(new OperationValue(operand2, operand1, it))
                }
            }
        }
        return newValues
    }
}

interface IValue {
    Float result();
}

class Value implements IValue {
    Float numValue

    Value(Integer value) {
        numValue = value
    }

    Float result() {
        return numValue
    }

    String toString() {
        return numValue
    }
}

class OperationValue implements IValue {
    IValue operand1
    IValue operand2
    String operation

    OperationValue(IValue operand1, IValue operand2, String operation) {
        this.operand1 = operand1
        this.operand2 = operand2
        this.operation = operation
    }

    Float result() {
        switch (operation) {
            case '+':
                return operand1.result() + operand2.result()
                break
            case '-':
                return operand1.result() - operand2.result()
                break
            case '*':
                return operand1.result() * operand2.result()
                break
            case '/':
                return operand1.result() / operand2.result()
                break
            default:
                println "Error! I don't know how to handle operation: ${op}"
        }
    }

    String toString() {
        return "(${operand1} ${operation} ${operand2})"
    }

    Boolean equals24() {
        Math.abs(result() - 24) < 0.1
    }
}

class OperandList {
    List<IValue> items = []
    Set<String> keys = []

    OperandList() {
        items = []
    }

    OperandList(List<IValue> items) {
        items.each {
            this.add(it)
        }
    }

    static newFromIntegers(List<Integer> integerList) {
        new OperandList(integerList.collect { new Value(it) })
    }

    OperandList removeAt(index) {
        List<IValue> itemsClone = items.clone()
        itemsClone.removeAt(index)
        OperandList copy = new OperandList(itemsClone)
        return copy
    }

    IValue first() {
        items.first()
    }

    IValue last() {
        items.last()
    }

    Integer size() {
        items.size()
    }

    OperandList add(OperationValue newItem) {
        if (!keys.contains(newItem.toString())) {
            keys.add(newItem.toString())
            items << newItem
        }
        this
    }

    OperandList add(Value newItem) {
        items << newItem
        this
    }

    OperandList add(OperandList newItems) {
        newItems.items.each { IValue item ->
            if (!item) {
                println "How can this item be null?"
            }
            this.add(item)
        }
        this
    }
}


Float calculate(operand1, operand2, operation) {
    switch (operation) {
        case '+':
            return operand1 + operand2
            break
        case '-':
            return operand1 - operand2
            break
        case '*':
            return operand1 * operand2
            break
        case '/':
            return operand1 / operand2
            break
        default:
            println "Error! I don't know how to handle operation: ${op}"
    }
}

class InputOutputSet {
    Map<String, List<Integer>> inputLists
    Map<String, IValue> solutions

    InputOutputSet() {
        inputLists = [:]
        solutions = [:]
    }

    void add(List<Integer> inputList, IValue solution) {
        String keyForList = createKeyForInputList(inputList)
        if (!inputLists.containsKey(keyForList)) {
            inputLists[keyForList] = inputList
            solutions[keyForList] = solution
        }
    }

    String createKeyForInputList(List<Integer> inputList) {
        Map<Integer, Integer> numberCounts = [:]
        inputList.each {
            if (!numberCounts.containsKey(it)) {
                numberCounts[it] = 0
            }
            numberCounts[it]++
        }
        String keyString = ''
        numberCounts.keySet().sort().each {
            keyString += "${it}:${numberCounts[it]}"
        }
        return keyString
    }
}


//new Solver().solve2(puzzleNumbers)

// Now instead of solving the problem, generate a puzzle, and find combinations with no solution

Integer maxIntegerInSearch = 9

def noSolution = []
def onlyOnePuzzleSolutions = new InputOutputSet()
Integer numSearched = 0

println "\n\nI'm searching ${Math.pow(maxIntegerInSearch, 4).toInteger()} different input sets..."

(1..maxIntegerInSearch).each { firstNumber ->
    (1..maxIntegerInSearch).each { secondNumber ->
        (1..maxIntegerInSearch).each { thirdNumber ->
            (1..maxIntegerInSearch).each { fourthNumber ->
                def inputNumbers = [firstNumber, secondNumber, thirdNumber, fourthNumber]
                OperandList solutions = (new Solver()).solve2(inputNumbers)
                if (solutions.size() == 0) {
                    noSolution << inputNumbers
                }
                if (solutions.size() == 1) {
                    onlyOnePuzzleSolutions.add(inputNumbers, solutions.first())
                }
                numSearched++
                if (numSearched % 100 == 0) {
                    println "I have searched ${numSearched} input sets so far..."
                }
            }
        }
    }
}

println "\n\nI searched ${Math.pow(maxIntegerInSearch, 4).toInteger()} different input sets."
println "I found ${noSolution.size()} puzzles with no solution"
println "I found ${onlyOnePuzzleSolutions.inputLists.size()} puzzles with only one solution"

println "The puzzles with only one solution are:"
onlyOnePuzzleSolutions.inputLists.eachWithIndex { inputSet ->
    println "\t${inputSet.value} -> ${onlyOnePuzzleSolutions.solutions[inputSet.key]}"
}


def inputNumbers = [3,3,3,4]
OperandList solutions = new Solver().solve2(inputNumbers)
println "\n\nI found ${solutions.size()} solutions for the input: ${inputNumbers}"
if (solutions.size() > 0) {
    println "Solutions were:"
    solutions.items.each {
        println "\t${it}"
    }
}
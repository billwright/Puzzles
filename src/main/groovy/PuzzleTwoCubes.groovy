Integer maxSearchValue = 30


def numbersOfTwoCubes = [:]

(1..maxSearchValue).each { firstCube ->
    (1..maxSearchValue).each { secondCube ->
        if (firstCube != secondCube) {
            Integer twoCubes = Math.pow(firstCube, 3) + Math.pow(secondCube, 3)

            if (!numbersOfTwoCubes.containsKey(twoCubes)) {
                numbersOfTwoCubes[twoCubes] = [[firstCube, secondCube]]
            } else {
                def addPair = true
                numbersOfTwoCubes[twoCubes].each { existingCubes ->
                    if (existingCubes.contains(firstCube) && existingCubes.contains(secondCube)) {
                        addPair = false     // Found this exact pair in the reverse order, so don't add them again.
                    }
                }
                if (addPair) {
                    numbersOfTwoCubes[twoCubes] << [firstCube, secondCube]
                }
            }
        }
    }
}

def twoDifferentCubeSums = numbersOfTwoCubes.findAll { key, value -> value.size() > 1 }

println "I found ${twoDifferentCubeSums.size()} numbers that can be represented as two different sums of cubes. They are:"
twoDifferentCubeSums.keySet().asList().sort().each { number ->
    println "\tNumber ${number}:"
    numbersOfTwoCubes[number].each {
        println "\t\t${it}"
    }
}

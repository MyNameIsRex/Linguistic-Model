import random

class Program:
    ans : int = 0
    playerAns : int = 0
    upperbound : int = 100
    lowerbound : int = 1

    success : bool = False

    #Gets called on instantiation, a constructor
    def __init__(self) -> None:
        self.ans = random.randint(1, 100)
        self.runProgram()
        
    def runProgram(self) -> None:
        while self.success == False:
            playerInput = input(f"Pick a number between {self.lowerbound} and {self.upperbound}\n")

            if playerInput.isdigit() == False:
                print("The input is not a number!")
                continue

            self.playerAns = int(playerInput)

            if self.playerAns >= self.upperbound or self.playerAns <= self.lowerbound:
                print("The input is out of bounds")
                continue

            self.success = self.playerAns == self.ans
            
            if self.success == False:
                self.updateValues(self.playerAns)
        
        print(f"You have guessed the answer, the answer is {self.ans}")

    def updateValues(self, playerAns : int) -> None:
        if playerAns < self.ans:
            self.lowerbound = playerAns
            return
        self.upperbound = playerAns

program : Program = Program()
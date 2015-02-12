class Variable:

    # label: string identifier
    # domain: list of potential integer values
    def __init__(self, label, domain):
        self.label = label
        self.domain = domain
class Constraint:

    # left: left Variable
    # right: right Variable
    # operator: string binary operator (left operator right)
    def __init__(self, left, right, operator):
        self.left = left
        self.right = right
        self.operator = operator

    # attempt to reduce the domain of the right
    def leftForwardCheck:
        # left should just have one value at this point
        value = self.left.domain[0]
        if self.operator == ">":
            filter(lambda x: x < value, self.right.domain)
        elif self.operator == "<":
            filter(lambda x: x > value, self.right.domain)
        elif self.operator == "!":
            filter(lambda x: x != value, self.right.domain)
        elif self.operator == "=":
            filter(lambda x: x == value, self.right.domain)
    # attempt to reduce the domain of the left
    def rightForwardCheck:
        # right should just have one value at this point
        value = self.right.domain[0]
        if self.operator == ">":
            filter(lambda x: x > value, self.left.domain)
        elif self.operator == "<":
            filter(lambda x: x < value, self.left.domain)
        elif self.operator == "!":
            filter(lambda x: x != value, self.left.domain)
        elif self.operator == "=":
            filter(lambda x: x == value, self.left.domain)
            
if __name__ == "__main__":

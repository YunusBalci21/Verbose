[line 17] Error at 'equals': Expect ';' after expression.
Consumed token: ;
Consumed token: ;
Consumed token: min
Consumed token: of_type
Consumed token: ;
Consumed token: max
Consumed token: of_type
Consumed token: ;
Consumed token: ;
Consumed token: average
Consumed token: of_type
Consumed token: )
[line 23] Error at '/': Expect ';' after variable declaration.
Consumed token: ;
Consumed token: ;
Consumed token: }
Consumed token: (
[line 34] Error at 'equals': Expect ')' after if condition.
Consumed token: ;
[line 36] Error at '}': Expect expression.
Consumed token: ;
[line 38] Error at '}': Expect expression.
Consumed token: (
[line 41] Error at 'equals': Expect ')' after if condition.
Consumed token: ;
[line 43] Error at '}': Expect expression.
Consumed token: ;
[line 45] Error at '}': Expect expression.
Consumed token: a
Consumed token: of_type
Consumed token: ;
Consumed token: (
Consumed token: )
Consumed token: ;
Consumed token: ;
[line 52] Error at '}': Expect expression.
Consumed token: }

org.opentest4j.AssertionFailedError: line 0 of source and target mismatch. Expected the content: VarDecl ==> 
Expected :true
Actual   :false
<Click to see difference>


	at org.junit.jupiter.api.AssertionFailureBuilder.build(AssertionFailureBuilder.java:151)
	at org.junit.jupiter.api.AssertionFailureBuilder.buildAndThrow(AssertionFailureBuilder.java:132)
	at org.junit.jupiter.api.AssertTrue.failNotTrue(AssertTrue.java:63)
	at org.junit.jupiter.api.AssertTrue.assertTrue(AssertTrue.java:36)
	at org.junit.jupiter.api.Assertions.assertTrue(Assertions.java:214)
	at ParserTest.testEquivalenceOfEachLine(ParserTest.java:71)
	at java.base/java.lang.reflect.Method.invoke(Method.java:580)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1597)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1597)


% AUTHOR: Maxwell Hall

% error check
oddmult3(X) :- not(integer(X)), write('ERROR: Parameter not an integer!').
oddmult3(X) :- integer(X), 0 is X mod 3, 1 is (X / 3) mod 2.

% base case
fact(0, Y) :- Y is 1, !.
% error check
fact(X, Y) :- not(integer(X)), write('ERROR: Parameter not an integer!'), !.
% recursive call
fact(X, Y) :- integer(X), A is X-1, fact(A, Z), Y is Z*X.

% predicates for use in segregate
even(X) :- integer(X), 0 is X mod 2.
odd(X) :- integer(X), 1 is X mod 2.

% base case
segregate([], Even, Odd) :- Even=[], Odd=[].
% recursive call
segregate([Head | Tail], Even, Odd) :- segregate(Tail, Even2, Odd2),
  ((even(Head), Even = [Head|Even2], Odd=Odd2) ; (odd(Head), Odd = [Head | Odd2], Even = Even2)), !.


% error check
swapends(Given, New) :- length(Given, Len), Len < 2, write('ERROR: List too short!'), !.
swapends([H|T], New) :- length(T, Len), Len+1 >= 2, swapendsHelper(T, New2, H, Tail),
  New = [Tail|New2], !.
% base case
swapendsHelper([H|T], New, Head, Tail) :- length(T, Len), Len = 0, Tail is H, New = [Head].
% recursive call
swapendsHelper([H|T], New, Head, Tail) :- length(T, Len), Len > 0, swapendsHelper(T, New2, Head, Tail),
  New = [H|New2].


bookends(Prefix, Suffix, Term) :- prefix(Prefix, Term), suffix(Suffix, Term).

% bookends helpers:
% base case
prefix([], Term).
% recursive call
prefix([HPrefix|TPrefix], [HTerm|TTerm]) :- HPrefix = HTerm, prefix(TPrefix, TTerm).

% base case
suffix([], Term).
% recursive call
suffix(Suffix, [HTerm|TTerm]) :- length(Suffix, LenS), length(TTerm, LenT), LenS < LenT+1, suffix(Suffix, TTerm), !.
% recursive call
suffix([HSuffix|TSuffix], [HTerm|TTerm]) :- length(TSuffix, LenS), length(TTerm, LenT), LenT = LenS, HSuffix = HTerm,
  suffix(TSuffix, TTerm), !.

% base case
subslice([], Term).
% success check / recursive call
subslice([HP | TP], [HT|TT]) :- ((HP = HT, prefix(TP, TT)) ; subslice([HP|TP], TT)), !.


cycle(X) :- path(X, A), path(A, X), !.

% luhn helper predicates:
sumDigits(0,0).
sumDigits(X, Sum) :- X > 0, Tail is (X mod 10), Remaining is floor(X/10), sumDigits(Remaining, Sum2), Sum is Tail+Sum2, !.

getLuhnSumEven(0, 0).
getLuhnSumEven(X, Sum) :- X > 0, Remaining is floor(X/10), getLuhnSumOdd(Remaining, Sum2), Tail is 2*(X mod 10),
  sumDigits(Tail, Dig), Sum is Sum2+Dig, !.

getLuhnSumOdd(0, 0).
getLuhnSumOdd(X, Sum) :- X > 0, Remaining is floor(X/10), getLuhnSumEven(Remaining, Sum2), Tail is (X mod 10),
  Sum is Sum2 + Tail, !.

% luhn predicate:
luhn(X) :- integer(X), getLuhnSumOdd(X, Sum), 0 is (Sum mod 10), !.

% clue knowledge base:
affairLiteral(mrBoddy, msGreen).
affairLiteral(missScarlet, mrBoddy).
marriedLiteral(profPlum, msGreen).
rich(mrBoddy).
greedy(colMustard).

% clue predicates
married(A, B) :- marriedLiteral(B, A) ; marriedLiteral(A, B).
affair(A, B) :- affairLiteral(A, B) ; affairLiteral(B, A).

suspect(Suspect, Victim) :- (married(Suspect, Spouse), affair(Victim, Spouse))
 ; (greedy(Suspect), not(rich(Suspect)), rich(Victim)).

% Single fact that will result in only one suspect:
% rich(colMustard).

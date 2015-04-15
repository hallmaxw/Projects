oddmult3(X) :- not(integer(X)), write('ERROR: Parameter not an integer!').
oddmult3(X) :- integer(X), 0 is X mod 3, 1 is (X / 3) mod 2.

fact(0, Y) :- Y is 1.
fact(X, Y) :- not(integer(X)), write('ERROR: Parameter not an integer!').
fact(X, Y) :- integer(X), A is X-1, fact(A, Z), Y is Z*X.

even(X) :- integer(X), 0 is X mod 2.

odd(X) :- integer(X), 1 is X mod 2.

segregate([], Even, Odd) :- Even=[], Odd=[].
segregate([Head | Tail], Even, Odd) :- segregate(Tail, Even2, Odd2),
  ((even(Head), Even = [Head|Even2], Odd=Odd2) ; (odd(Head), Odd = [Head | Odd2], Even = Even2)).



swapends(Given, New) :- length(Given, Len), Len < 2, write('ERROR: List too short!').
swapends([H|T], New) :- length(T, Len), Len+1 >= 2, swapendsHelper(T, New2, H, Tail),
  New = [Tail|New2].

swapendsHelper([H|T], New, Head, Tail) :- length(T, Len), Len = 0, Tail is H, New = [Head].
swapendsHelper([H|T], New, Head, Tail) :- length(T, Len), Len > 0, swapendsHelper(T, New2, Head, Tail),
  New = [H|New2].

bookends(Prefix, Suffix, Term) :- prefix(Prefix, Term), suffix(Suffix, Term).

prefix([], Term).
prefix([HPrefix|TPrefix], [HTerm|TTerm]) :- HPrefix = HTerm, prefix(TPrefix, TTerm).

suffix([], Term).
suffix(Suffix, [HTerm|TTerm]) :- length(Suffix, LenS), length(TTerm, LenT), LenS < LenT+1, suffix(Suffix, TTerm).
suffix([HSuffix|TSuffix], [HTerm|TTerm]) :- length(TSuffix, LenS), length(TTerm, LenT), LenT = LenP, HSuffix = HTerm.

subslice([], Term).
subslice([HP | TP], [HT|TT]) :- (HP = HT, prefix(TP, TT)) ; subslice([HP|TP], TT).

edge(a,b).
edge(b,c).
edge(c,d).
edge(d,a).
edge(d,e).
edge(b,a).
path(A, B) :- edge(A, B), !.
path(A, B) :- edge(A, X), path(X, B), !.

cycle(X) :- path(X, A), path(A, X), !.




affairLiteral(mrBoddy, msGreen).
affairLiteral(missScarlet, mrBoddy).
marriedLiteral(profPlum, msGreen).
rich(mrBoddy).
greedy(colMustard).

married(A, B) :- marriedLiteral(B, A) ; marriedLiteral(A, B).
affair(A, B) :- affairLiteral(A, B) ; affairLiteral(B, A).

motive(Suspect, Victim) :- (married(Suspect, Spouse), affair(Victim, Spouse))
 ; (greedy(Suspect), not(rich(Suspect)), rich(Victim)).

suspect(Suspect, Victim) :- motive(Suspect, Victim).

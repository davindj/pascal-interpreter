var
	x:integer;
	a,b,c:integer;
begin
	x:=1+2;
	writeln('1 + 2 = ' + x);
	x:=1-2;
	writeln('1 - 2 = ' + x);
	x:=1*2;
	writeln('1 * 2 = ' + x);
	x:=1/2;
	writeln('1 / 2 = ' + x);
	x:=1%2;
	writeln('1 % 2 = ' + x);
	x:=2^3;
	writeln('2 ^ 3 = ' + x);
	x:=-2*1+3;
	writeln('-2 * 1 + 3 = ' + x);

	a:=1;
	b:=2;
	c:=3;
	x:=b*(a+c);
	writeln('a:1, b:2, c:3');
	writeln('b*(a+c) = ' + x);
end.

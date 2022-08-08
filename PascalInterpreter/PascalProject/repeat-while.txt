var
	x:integer;
begin
	writeln('Repeat While Program');
	x:=0;
	repeat
		begin
			x:=x+1;
			writeln(x);
		end
	while (x<10);
end.

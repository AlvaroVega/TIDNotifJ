echo
echo start do-creation

echo
run-serie-100.ksh 100 &
sleep 5

echo
echo "1 - (1,0) - (1,0) - 1 - 1  [25]"
test.ksh 100
echo done.

echo
echo "1 - (1,0) - (1,0) - 1 - 10  [5]"
test.ksh 101
echo done.

echo
echo "1 - (1,0) - (1,0) - 10 - 1  [5]"
test.ksh 111
echo done.

echo
echo "1 - (1,0) - (1,9) - 1 - 1  [5]"
test.ksh 121
echo done.

echo
echo "1 - (1,9) - (1,0) - 1 - 1  [5]"
test.ksh 131
echo done.

echo
echo "10 - (1,0) - (1,0) - 1 - 1  [5]"
test.ksh 141
echo done.

echo
echo "10 - (1,0) - (1,0) - 1 - 10  [1]"
test.ksh 142
echo done.

echo
echo "10 - (1,0) - (1,0) - 10 - 1  [1]"
test.ksh 143
echo done.

echo
echo "10 - (1,0) - (1,9) - 1 - 1  [1]"
test.ksh 144
echo done.

echo
echo "10 - (1,9) - (1,0) - 1 - 1  [1]"
test.ksh 145
echo done.

echo
echo "3 - (1,2) - (1,2) - 3 - 3  [2]"
test.ksh 151
echo done.

#theend.ksh


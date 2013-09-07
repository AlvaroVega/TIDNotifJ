run-serie-200.ksh 213 &
sleep 5
echo
echo "1 - (1,0) - (1,0) - 10 - 1  [2000]"
test.ksh 213
theend.ksh
echo done.

echo
run-serie-200.ksh 223 &
sleep 5
echo
echo "1 - (1,0) - (1,0) - 20 - 1  [1000]"
test.ksh 223
theend.ksh
echo done.

echo
run-serie-200.ksh 233 &
sleep 5
echo
echo "1 - (1,0) - (1,0) - 50 - 1  [500]"
test.ksh 233
theend.ksh
echo done.

echo
run-serie-200.ksh 243 &
sleep 5
echo
echo "1 - (1,0) - (1,0) - 1 - 10  [2000]"
test.ksh 243
theend.ksh
echo done.

echo
run-serie-200.ksh 253 &
sleep 5
echo
echo "1 - (1,0) - (1,0) - 1 - 20  [1000]"
test.ksh 253
theend.ksh
echo done.

echo
run-serie-200.ksh 263 &
sleep 5
echo
echo "1 - (1,0) - (1,0) - 1 - 50  [500]"
test.ksh 263
theend.ksh
echo done.

echo
run-serie-200.ksh 273 &
sleep 5
echo
echo "1 - (1,0) - (1,0) - 10 - 10  [1000]"
test.ksh 273
theend.ksh
echo done.


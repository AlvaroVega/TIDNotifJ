echo
echo start do_serie_000.ksh

echo
run-serie-000.ksh 000 &
sleep 5
echo
echo "1 - (1,0) - (1,0) - 1 - 1  [2500]"
test.ksh 000
theend.ksh
echo done.

echo
run-serie-000.ksh 001 &
sleep 5
echo
echo "1 - (1,0) - (1,0) - 1 - 10  [250]"
test.ksh 001
theend.ksh
echo done.

echo
run-serie-000.ksh 011 &
sleep 5
echo
echo "1 - (1,0) - (1,0) - 10 - 1  [250]"
test.ksh 011
theend.ksh
echo done.

echo
run-serie-000.ksh 021 &
sleep 5
echo
echo "1 - (1,0) - (1,9) - 1 - 1  [250]"
test.ksh 021
theend.ksh
echo done.

echo
run-serie-000.ksh 031 &
sleep 5
echo
echo "1 - (1,9) - (1,0) - 1 - 1  [250]"
test.ksh 031
theend.ksh
echo done.

echo
run-serie-000.ksh 041 &
sleep 5
echo
echo "10 - (1,0) - (1,0) - 1 - 1  [250]"
test.ksh 041
theend.ksh
echo done.

echo
run-serie-000.ksh 042 &
sleep 5
echo
echo "10 - (1,0) - (1,0) - 1 - 10  [25]"
test.ksh 042
theend.ksh
echo done.

echo
run-serie-000.ksh 043 &
sleep 5
echo
echo "10 - (1,0) - (1,0) - 10 - 1  [25]"
test.ksh 043
theend.ksh
echo done.

echo
run-serie-000.ksh 044 &
sleep 5
echo
echo "10 - (1,0) - (1,9) - 1 - 1  [25]"
test.ksh 044
theend.ksh
echo done.

echo
run-serie-000.ksh 045 &
sleep 5
echo
echo "10 - (1,9) - (1,0) - 1 - 1  [25]"
test.ksh 045
theend.ksh
echo done.

echo
run-serie-000.ksh 051 &
sleep 5
echo
echo "3 - (1,2) - (1,2) - 3 - 3  [75]"
test.ksh 051
theend.ksh
echo done.


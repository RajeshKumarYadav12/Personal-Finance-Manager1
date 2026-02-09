BASE_URL="http://localhost:8080"
SESSION_COOKIE=""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

PASSED=0
FAILED=0

# Function to make authenticated requests
auth_request() {
    local method=$1
    local endpoint=$2
    local data=$3
    
    if [ -n "$data" ]; then
        curl -s -X "$method" \
            -H "Content-Type: application/json" \
            -H "Cookie: $SESSION_COOKIE" \
            -d "$data" \
            "$BASE_URL$endpoint"
    else
        curl -s -X "$method" \
            -H "Cookie: $SESSION_COOKIE" \
            "$BASE_URL$endpoint"
    fi
}

# Function to test endpoint
test_endpoint() {
    local test_name=$1
    local method=$2
    local endpoint=$3
    local data=$4
    local expected_status=$5
    
    echo -n "Testing: $test_name... "
    
    if [ -n "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X "$method" \
            -H "Content-Type: application/json" \
            -H "Cookie: $SESSION_COOKIE" \
            -d "$data" \
            "$BASE_URL$endpoint")
    else
        response=$(curl -s -w "\n%{http_code}" -X "$method" \
            -H "Cookie: $SESSION_COOKIE" \
            "$BASE_URL$endpoint")
    fi
    
    status=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$status" == "$expected_status" ]; then
        echo -e "${GREEN}PASSED${NC} (Status: $status)"
        PASSED=$((PASSED + 1))
        echo "$body"
    else
        echo -e "${RED}FAILED${NC} (Expected: $expected_status, Got: $status)"
        FAILED=$((FAILED + 1))
        echo "Response: $body"
    fi
    echo ""
}

echo "=========================================="
echo "Financial Manager API Test Suite"
echo "=========================================="
echo ""

# Test 1: Register User
echo "1. AUTHENTICATION TESTS"
echo "----------------------"

response=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d '{"username":"testuser","password":"password123"}' \
    "$BASE_URL/auth/register")

status=$(echo "$response" | tail -n1)
echo -n "Register User... "
if [ "$status" == "201" ]; then
    echo -e "${GREEN}PASSED${NC}"
    PASSED=$((PASSED + 1))
else
    echo -e "${RED}FAILED${NC} (Status: $status)"
    FAILED=$((FAILED + 1))
fi

# Test 2: Login
response=$(curl -s -w "\n%{http_code}" -i -X POST \
    -H "Content-Type: application/json" \
    -d '{"username":"testuser","password":"password123"}' \
    "$BASE_URL/auth/login")

status=$(echo "$response" | tail -n1)
SESSION_COOKIE=$(echo "$response" | grep -i "Set-Cookie:" | sed 's/Set-Cookie: //' | cut -d';' -f1)

echo -n "Login User... "
if [ "$status" == "200" ] && [ -n "$SESSION_COOKIE" ]; then
    echo -e "${GREEN}PASSED${NC}"
    PASSED=$((PASSED + 1))
else
    echo -e "${RED}FAILED${NC} (Status: $status)"
    FAILED=$((FAILED + 1))
fi
echo ""

# Test 3: Categories
echo "2. CATEGORY TESTS"
echo "-----------------"

test_endpoint "Get All Categories" "GET" "/categories" "" "200"
test_endpoint "Create Custom Category" "POST" "/categories" '{"name":"Groceries"}' "201"
test_endpoint "Create Duplicate Category" "POST" "/categories" '{"name":"Groceries"}' "409"

# Test 4: Transactions
echo "3. TRANSACTION TESTS"
echo "--------------------"

test_endpoint "Create Income Transaction" "POST" "/transactions" \
    '{"description":"Salary","amount":5000,"type":"INCOME","date":"2024-02-01","categoryId":1}' "201"

test_endpoint "Create Expense Transaction" "POST" "/transactions" \
    '{"description":"Groceries","amount":150,"type":"EXPENSE","date":"2024-02-05","categoryId":4}' "201"

test_endpoint "Get All Transactions" "GET" "/transactions" "" "200"

test_endpoint "Get Transactions with Filter" "GET" "/transactions?type=INCOME" "" "200"

test_endpoint "Update Transaction" "PUT" "/transactions/1" \
    '{"description":"Updated Salary","amount":5500,"type":"INCOME","date":"2024-02-01","categoryId":1}' "200"

test_endpoint "Get Transaction by ID" "GET" "/transactions/1" "" "200"

# Test 5: Savings Goals
echo "4. SAVINGS GOAL TESTS"
echo "---------------------"

test_endpoint "Create Savings Goal" "POST" "/goals" \
    '{"name":"Emergency Fund","targetAmount":10000,"startDate":"2024-01-01","targetDate":"2024-12-31"}' "201"

test_endpoint "Get All Goals" "GET" "/goals" "" "200"

test_endpoint "Get Goal by ID" "GET" "/goals/1" "" "200"

test_endpoint "Update Goal" "PUT" "/goals/1" \
    '{"name":"Updated Emergency Fund","targetAmount":15000,"startDate":"2024-01-01","targetDate":"2024-12-31"}' "200"

# Test 6: Reports
echo "5. REPORT TESTS"
echo "---------------"

test_endpoint "Monthly Report" "GET" "/reports/monthly?year=2024&month=2" "" "200"

test_endpoint "Yearly Report" "GET" "/reports/yearly?year=2024" "" "200"

# Test 7: Authorization Tests
echo "6. AUTHORIZATION TESTS"
echo "----------------------"

# Register second user
curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{"username":"testuser2","password":"password123"}' \
    "$BASE_URL/auth/register" > /dev/null

# Login as second user
response=$(curl -s -i -X POST \
    -H "Content-Type: application/json" \
    -d '{"username":"testuser2","password":"password123"}' \
    "$BASE_URL/auth/login")

SESSION_COOKIE2=$(echo "$response" | grep -i "Set-Cookie:" | sed 's/Set-Cookie: //' | cut -d';' -f1)

# Try to access first user's transaction
echo -n "Access Another User's Transaction... "
response=$(curl -s -w "\n%{http_code}" -X GET \
    -H "Cookie: $SESSION_COOKIE2" \
    "$BASE_URL/transactions/1")

status=$(echo "$response" | tail -n1)
if [ "$status" == "404" ] || [ "$status" == "403" ]; then
    echo -e "${GREEN}PASSED${NC} (Properly denied)"
    PASSED=$((PASSED + 1))
else
    echo -e "${RED}FAILED${NC} (Status: $status - should be 403 or 404)"
    FAILED=$((FAILED + 1))
fi

# Test 8: Delete operations
echo ""
echo "7. DELETE TESTS"
echo "---------------"

# Switch back to first user
SESSION_COOKIE=$SESSION_COOKIE

test_endpoint "Delete Transaction" "DELETE" "/transactions/2" "" "204"

test_endpoint "Delete Goal" "DELETE" "/goals/1" "" "204"

test_endpoint "Delete Category with No Transactions" "DELETE" "/categories/10" "" "204"

# Test 9: Logout
echo "8. LOGOUT TEST"
echo "--------------"

test_endpoint "Logout User" "POST" "/auth/logout" "" "200"

# Test 10: Unauthorized Access
echo "9. UNAUTHORIZED ACCESS TEST"
echo "---------------------------"

echo -n "Access Protected Endpoint Without Auth... "
response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/transactions")
status=$(echo "$response" | tail -n1)
if [ "$status" == "401" ]; then
    echo -e "${GREEN}PASSED${NC}"
    PASSED=$((PASSED + 1))
else
    echo -e "${RED}FAILED${NC} (Status: $status)"
    FAILED=$((FAILED + 1))
fi

# Summary
echo ""
echo "=========================================="
echo "TEST SUMMARY"
echo "=========================================="
echo -e "Total Passed: ${GREEN}$PASSED${NC}"
echo -e "Total Failed: ${RED}$FAILED${NC}"
echo "=========================================="

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}Some tests failed!${NC}"
    exit 1
fi

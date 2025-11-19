#!/bin/bash

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check for required commands
if ! command_exists curl || ! command_exists jq; then
    echo "Error: Required commands 'curl' and 'jq' are not installed." >&2
    exit 1
fi

BASE_URL="http://localhost:8080/api"
USER_ID=""
EVENT_ID=""
BET_ID=""

# --- Test Case 1: Register a New User ---
echo "--- Test Case 1: Register a New User ---"
register_payload=$(cat <<EOF
{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password"
}
EOF
)

register_response=$(curl -s -X POST -H "Content-Type: application/json" -d "$register_payload" "$BASE_URL/users/register")

if [[ $(echo "$register_response" | jq -r '.message') == "User registered successfully" ]]; then
    echo "PASS: Register User"
    USER_ID=$(echo "$register_response" | jq -r '.userId')
else
    echo "FAIL: Register User"
    echo "Response: $register_response"
    exit 1
fi

# --- Test Case 2: Login ---
echo "--- Test Case 2: Login ---"
login_payload=$(cat <<EOF
{
    "username": "testuser",
    "password": "password"
}
EOF
)

login_response=$(curl -s -X POST -H "Content-Type: application/json" -d "$login_payload" "$BASE_URL/users/login")

if [[ $(echo "$login_response" | jq -r '.id') == "$USER_ID" ]]; then
    echo "PASS: Login"
else
    echo "FAIL: Login"
    echo "Response: $login_response"
    exit 1
fi

# --- Test Case 3: Create an Event ---
echo "--- Test Case 3: Create an Event ---"
create_event_payload=$(cat <<EOF
{
    "homeTeam": "Team A",
    "awayTeam": "Team B",
    "startTime": "$(date -u +"%Y-%m-%dT%H:%M:%S")",
    "homeWinOdds": 1.5,
    "awayWinOdds": 2.5,
    "drawOdds": 3.0
}
EOF
)

create_event_response=$(curl -s -X POST -H "Content-Type: application/json" -d "$create_event_payload" "$BASE_URL/events")

if [[ $(echo "$create_event_response" | jq -r '.id') != "null" ]]; then
    echo "PASS: Create Event"
    EVENT_ID=$(echo "$create_event_response" | jq -r '.id')
else
    echo "FAIL: Create Event"
    echo "Response: $create_event_response"
    exit 1
fi

# --- Test Case 4: Place a Bet ---
echo "--- Test Case 4: Place a Bet ---"
place_bet_payload=$(cat <<EOF
{
    "userId": $USER_ID,
    "eventId": $EVENT_ID,
    "type": "WIN_HOME",
    "amount": 100,
    "odds": 2.10
}
EOF
)

place_bet_response=$(curl -s -X POST -H "Content-Type: application/json" -d "$place_bet_payload" "$BASE_URL")
place_bet_response=$(curl -s -X POST -H "Content-Type: application/json" -d "$place_bet_payload" "$BASE_URL/bets")

if [[ $(echo "$place_bet_response" | jq -r '.message') == "Bet placed successfully" ]]; then
    echo "PASS: Place Bet"
    BET_ID=$(echo "$place_bet_response" | jq -r '.betId')
else
    echo "FAIL: Place Bet"
    echo "Response: $place_bet_response"
    exit 1
fi

# --- Test Case 2: Get User Bets ---
echo "--- Test Case 2: Get User Bets ---"
get_user_bets_response=$(curl -s -X GET "$BASE_URL/user/$USER_ID")

if [[ $(echo "$get_user_bets_response" | jq --argjson bet_id "$BET_ID" '.[] | select(.id == $bet_id) | .id') == "$BET_ID" ]]; then
    echo "PASS: Get User Bets"
else
    echo "FAIL: Get User Bets"
    echo "Response: $get_user_bets_response"
    exit 1
fi

# --- Test Case 3: Get Event Bets ---
echo "--- Test Case 3: Get Event Bets ---"
get_event_bets_response=$(curl -s -X GET "$BASE_URL/event/$EVENT_ID")

if [[ $(echo "$get_event_bets_response" | jq --argjson bet_id "$BET_ID" '.[] | select(.id == $bet_id) | .id') == "$BET_ID" ]]; then
    echo "PASS: Get Event Bets"
else
    echo "FAIL: Get Event Bets"
    echo "Response: $get_event_bets_response"
    exit 1
fi

# --- Test Case 4: Get Pending Bets ---
echo "--- Test Case 4: Get Pending Bets ---"
get_pending_bets_response=$(curl -s -X GET "$BASE_URL/pending")

if [[ $(echo "$get_pending_bets_response" | jq --argjson bet_id "$BET_ID" '.[] | select(.id == $bet_id) | .id') == "$BET_ID" ]]; then
    echo "PASS: Get Pending Bets"
else
    echo "FAIL: Get Pending Bets"
    echo "Response: $get_pending_bets_response"
    exit 1
fi

# --- Test Case 5: Settle a Bet ---
echo "--- Test Case 5: Settle a Bet ---"
settle_bet_payload=$(cat <<EOF
{
    "result": "HOME_WIN"
}
EOF
)

settle_bet_response=$(curl -s -X PUT -H "Content-Type: application/json" -d "$settle_bet_payload" "$BASE_URL/$BET_ID/settle")

if [[ $(echo "$settle_bet_response" | jq -r '.message') == "Bet settled" ]]; then
    echo "PASS: Settle Bet"
else
    echo "FAIL: Settle Bet"
    echo "Response: $settle_bet_response"
    exit 1
fi

echo "--- All tests passed successfully! ---"

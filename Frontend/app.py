import streamlit as st
import requests

# --- PAGE SETUP ---
st.set_page_config(page_title="DevCollab - Real-Time AI Kanban", layout="wide")

st.title("📋 DevCollab — AI Kanban Board")
st.caption("Live Integration: Streamlit (Student 2) ⇄ Ngrok ⇄ Spring Boot (Student 1) ⇄ MongoDB")

# --- LIVE BACKEND URL (STUDENT 1 NGROK TUNNEL) ---
# --- LIVE BACKEND URL (STUDENT 1 SERVEO TUNNEL) ---
BACKEND_URL = "https://5a22055dca149ee6-157-50-156-111.serveousercontent.com"

# Required for Ngrok free tier to bypass initial browser warning page
HEADERS = {
    "ngrok-skip-browser-warning": "true"
}

# --- INITIAL SESSION STATES ---
if "jwt_token" not in st.session_state:
    st.session_state.jwt_token = None

if "tasks" not in st.session_state:
    st.session_state.tasks = [
        {"id": "1", "title": "Setup MongoDB", "description": "Configure task collection", "status": "TODO"},
        {"id": "2", "title": "Build Spring Controller", "description": "STOMP endpoints", "status": "IN_PROGRESS"},
        {"id": "3", "title": "Streamlit Frontend UI", "description": "Kanban columns", "status": "DONE"}
    ]

# --- SIDEBAR: AUTHENTICATION ---
st.sidebar.header("🔐 User Authentication")

if st.session_state.jwt_token is None:
    auth_mode = st.sidebar.radio("Select Action", ["Login", "Register"])

    if auth_mode == "Register":
        username = st.sidebar.text_input("Username")
        email = st.sidebar.text_input("Email")
        password = st.sidebar.text_input("Password", type="password")

        if st.sidebar.button("Register Account"):
            if username and email and password:
                try:
                    payload = {"username": username, "email": email, "password": password}
                    res = requests.post(
                        f"{BACKEND_URL}/api/auth/register", 
                        json=payload, 
                        headers=HEADERS, 
                        timeout=5
                    )
                    if res.status_code in [200, 201]:
                        st.sidebar.success("Registration successful! You can now log in.")
                    else:
                        st.sidebar.error(f"Registration failed (HTTP {res.status_code}): {res.text}")
                except Exception as e:
                    st.sidebar.error(f"Cannot reach auth server: {e}")
            else:
                st.sidebar.error("Username, Email, and Password required!")

    elif auth_mode == "Login":
        login_email = st.sidebar.text_input("Email")
        password = st.sidebar.text_input("Password", type="password")

        if st.sidebar.button("Login"):
            if login_email and password:
                try:
                    # Payload matching Postman test success
                    payload = {"email": login_email, "password": password}
                    res = requests.post(
                        f"{BACKEND_URL}/api/auth/login", 
                        json=payload, 
                        headers=HEADERS, 
                        timeout=5
                    )
                    if res.status_code == 200:
                        data = res.json()
                        # Extract token from response (checks common response field names)
                        token = data.get("token") or data.get("jwt") or data.get("accessToken")
                        
                        if token:
                            st.session_state.jwt_token = token
                            st.sidebar.success("Logged in successfully!")
                            st.rerun()
                        else:
                            st.sidebar.error("Login succeeded, but token field was not found in response!")
                    else:
                        st.sidebar.error(f"Login failed (HTTP {res.status_code}): {res.text}")
                except Exception as e:
                    st.sidebar.error(f"Auth server offline: {e}")
            else:
                st.sidebar.error("Email and Password required!")

else:
    st.sidebar.success("🔑 Authenticated with JWT")
    if st.sidebar.button("Logout"):
        st.session_state.jwt_token = None
        st.rerun()

st.sidebar.markdown("---")

# --- SIDEBAR: FETCH / SYNC DATA ---
st.sidebar.header("Backend Connection")
if st.sidebar.button("🔄 Sync with MongoDB"):
    try:
        req_headers = HEADERS.copy()
        if st.session_state.jwt_token:
            req_headers["Authorization"] = f"Bearer {st.session_state.jwt_token}"

        res = requests.get(f"{BACKEND_URL}/api/tasks", headers=req_headers, timeout=5)
        if res.status_code == 200:
            st.session_state.tasks = res.json()
            st.sidebar.success("Synced with MongoDB!")
        elif res.status_code in [401, 403]:
            st.sidebar.error("Unauthorized! Please log in first.")
        else:
            st.sidebar.error(f"Failed to fetch tasks (HTTP {res.status_code})")
    except Exception as e:
        st.sidebar.warning(f"Backend offline: Using fallback data. ({e})")

# --- SIDEBAR: ADD TASK ---
st.sidebar.markdown("---")
st.sidebar.header("➕ Add New Task")
new_title = st.sidebar.text_input("Task Title", placeholder="e.g., Fix Auth Middleware")
new_desc = st.sidebar.text_area("Description", placeholder="MongoDB Task Details...")

if st.sidebar.button("Create Task"):
    if new_title:
        new_task = {
            "title": new_title,
            "description": new_desc,
            "status": "TODO"
        }
        
        if st.session_state.jwt_token:
            try:
                req_headers = HEADERS.copy()
                req_headers["Authorization"] = f"Bearer {st.session_state.jwt_token}"
                requests.post(f"{BACKEND_URL}/api/tasks", json=new_task, headers=req_headers, timeout=5)
            except Exception:
                pass
                
        st.session_state.tasks.append(new_task)
        st.sidebar.success(f"Task '{new_title}' added!")
        st.rerun()
    else:
        st.sidebar.error("Title is required!")
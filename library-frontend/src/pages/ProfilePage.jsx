import { useEffect, useState  } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

function ProfilePage({ readerId, readerName, onLogout }) {

    const [loans, setLoans] = useState([]);
    const [books, setBooks] = useState([]);

    const navigate = useNavigate();

    const handleLogout = () => {
    onLogout();
    navigate("/login");
};

  useEffect(() => {
      loadData();
  }, [readerId]);

    const loadData = async () => {

        try {

            const loansRes = await axios.get(
                "http://localhost:8080/loans?page=0&size=100"
            );

            const booksRes = await axios.get(
                "http://localhost:8080/books?page=0&size=100"
            );

            setLoans(loansRes.data);
            setBooks(booksRes.data);

        } catch (error) {
            console.error(error);
        }
    };


    const getBookTitle = (id) => {

        const book = books.find(b => b.id === id);
        return book ? book.title : "Unknown";
    };

   const myLoans = loans.filter(
       loan => loan.readerId === Number(readerId)
   );

return (

    <div className="page">

        <div className="profile-header">

            <h1>My Profile — {readerName}</h1>

            <button
                className="logout-btn"
                onClick={handleLogout}
            >
                Logout
            </button>

        </div>

        {/* Loans */}
        <h2>My Loans</h2>

        <div className="books-grid">

            {myLoans.map(loan => (

                <div
                    className="book-card"
                    key={loan.id}
                >

                    <h3>
                        {getBookTitle(loan.bookId)}
                    </h3>

                    <p>
                        Issue date: {loan.issueDate}
                    </p>

                    <p>
                        Return date: {loan.returnDate || "not returned"}
                    </p>

                </div>
            ))}

        </div>

    </div>
);
}

export default ProfilePage;

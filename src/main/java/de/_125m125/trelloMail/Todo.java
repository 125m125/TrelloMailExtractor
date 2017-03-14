package de._125m125.trelloMail;

import java.util.Date;

public class Todo {
    private final String title;
    private final String content;
    private final Date   d;

    public Todo(final String title, final String content, final Date d) {
        super();
        this.title = title;
        this.content = content.trim();
        this.d = d;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public Date getDate() {
        return this.d;
    }

    @Override
    public String toString() {
        return super.toString() + "{title:" + this.title + "; content=" + this.content + "; d=" + this.d + "}";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.content == null) ? 0 : this.content.hashCode());
        result = prime * result + ((this.d == null) ? 0 : this.d.hashCode());
        result = prime * result + ((this.title == null) ? 0 : this.title.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Todo other = (Todo) obj;
        if (this.content == null) {
            if (other.content != null)
                return false;
        } else if (!this.content.equals(other.content))
            return false;
        if (this.d == null) {
            if (other.d != null)
                return false;
        } else if (!this.d.equals(other.d))
            return false;
        if (this.title == null) {
            if (other.title != null)
                return false;
        } else if (!this.title.equals(other.title))
            return false;
        return true;
    }
}

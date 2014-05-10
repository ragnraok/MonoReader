from mono.database import db

class ModelMixin(object):
    def save(self):
        db.session.add(self)
        db.session.commit()
        return self

    def delete(self):
        db.session.delete(self)
        db.session.commit()
        return self

    def delete_without_commit(self):
        db.session.delete(self)

    def save_without_commit(self):
        db.session.add(self)

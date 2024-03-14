package com.nstuproject.studentcontrol.repository.student

import com.nstuproject.studentcontrol.db.AppDb
import javax.inject.Inject

class LocalStudentRepository @Inject constructor(
    private val db: AppDb,
) : StudentRepository {

}
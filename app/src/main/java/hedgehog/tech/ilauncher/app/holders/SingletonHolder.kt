package hedgehog.tech.ilauncher.app.holders

/**
    реализация паттерна блокировки с двойной проверкой,
    для исключения возможности состояния гонки в многопоточных средах
    (логика синглтона, который также содержит экземпляр синглтона)

    Необходим для передачи параметра внутрь синглтона
*/

open class SingletonHolder<out T, in A>(
    private val constructor: (A) -> T
) {

    @Volatile // видимость для других потоков
    private var instance: T? = null

    fun getInstance(arg: A): T {
        return when {
            instance != null -> instance!!
            else -> synchronized(this) {
                if (instance == null) instance = constructor(arg)
                instance!!
            }
        }
    }

}

